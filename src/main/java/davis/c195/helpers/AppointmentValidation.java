package davis.c195.helpers;

import davis.c195.model.Appointment;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;

/**
 * Helper class for validating appointments, particularly for checking overlapping schedules
 */
public class AppointmentValidation {

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