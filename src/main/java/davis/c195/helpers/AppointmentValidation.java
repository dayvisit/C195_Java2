package davis.c195.helpers;

import davis.c195.model.Appointment;
import javafx.collections.ObservableList;
import java.time.*;

/**
 * Helper class for validating appointments, particularly for checking overlapping schedules
 * and business hours compliance
 */
public class AppointmentValidation {

    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");
    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(22, 0);

    /**
     * Checks if appointment is within business hours (8:00 AM to 10:00 PM EST)
     * @param proposedStart Start time of proposed appointment
     * @param proposedEnd End time of proposed appointment
     * @return true if within business hours, false if outside
     */
    public static boolean isWithinBusinessHours(LocalDateTime proposedStart, LocalDateTime proposedEnd) {
        // Validate basic time logic
        if (proposedStart.isAfter(proposedEnd) || proposedStart.isEqual(proposedEnd)) {
            return false;
        }

        // Convert local times to EST for business hour check
        ZonedDateTime startZDT = proposedStart.atZone(ZoneId.systemDefault());
        ZonedDateTime endZDT = proposedEnd.atZone(ZoneId.systemDefault());
        ZonedDateTime estStartZDT = startZDT.withZoneSameInstant(EST_ZONE);
        ZonedDateTime estEndZDT = endZDT.withZoneSameInstant(EST_ZONE);

        // Check if appointment falls on weekend
        DayOfWeek startDay = estStartZDT.getDayOfWeek();
        DayOfWeek endDay = estEndZDT.getDayOfWeek();
        if (startDay == DayOfWeek.SATURDAY || startDay == DayOfWeek.SUNDAY ||
                endDay == DayOfWeek.SATURDAY || endDay == DayOfWeek.SUNDAY) {
            return false;
        }

        // Check if appointment is within business hours
        LocalTime estStartTime = estStartZDT.toLocalTime();
        LocalTime estEndTime = estEndZDT.toLocalTime();

        return !estStartTime.isBefore(BUSINESS_START) && !estEndTime.isAfter(BUSINESS_END);
    }

    /**
     * Checks if a proposed appointment time overlaps with any existing appointments for the same customer
     * @param appointments List of existing appointments to check against
     * @param customerId ID of customer to check appointments for
     * @param proposedStart Start time of proposed appointment
     * @param proposedEnd End time of proposed appointment
     * @param excludeAppointmentId Optional appointment ID to exclude from check (for updates)
     * @return true if no overlap, false if overlap exists
     */
    public static boolean isValidAppointmentTime(ObservableList<Appointment> appointments,
                                                 int customerId,
                                                 LocalDateTime proposedStart,
                                                 LocalDateTime proposedEnd,
                                                 Integer excludeAppointmentId) {

        // Validate basic time logic
        if (proposedStart.isAfter(proposedEnd) || proposedStart.isEqual(proposedEnd)) {
            return false;
        }

        // Check for overlaps with existing appointments
        for (Appointment existing : appointments) {
            // Skip if this is the appointment being updated
            if (excludeAppointmentId != null && existing.getAppointmentId() == excludeAppointmentId) {
                continue;
            }

            // Only check appointments for the same customer
            if (existing.getCustomerId() == customerId) {
                LocalDateTime existingStart = existing.getStart();
                LocalDateTime existingEnd = existing.getEnd();

                // Check for overlaps
                boolean overlaps = !(proposedEnd.isEqual(existingStart) || proposedEnd.isBefore(existingStart) ||
                        proposedStart.isEqual(existingEnd) || proposedStart.isAfter(existingEnd));

                if (overlaps) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Overloaded method for new appointments (no exclusion)
     */
    public static boolean isValidAppointmentTime(ObservableList<Appointment> appointments,
                                                 int customerId,
                                                 LocalDateTime proposedStart,
                                                 LocalDateTime proposedEnd) {
        return isValidAppointmentTime(appointments, customerId, proposedStart, proposedEnd, null);
    }
}