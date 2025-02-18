package davis.c195.model;

import java.time.LocalDateTime;

/**
 * Model class for Appointment objects that matches the database schema
 */
public class Appointment {
    private int appointmentId;
    private int customerId;
    private int userId;
    private int contactId;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Constructor for Appointment
     * @param appointmentId The appointment ID
     * @param customerId The customer ID
     * @param userId The user ID
     * @param contactId The contact ID
     * @param title The appointment title
     * @param description The appointment description
     * @param location The appointment location
     * @param type The appointment type
     * @param start The start date/time
     * @param end The end date/time
     */
    public Appointment(int appointmentId, int customerId, int userId, int contactId,
                       String title, String description, String location, String type,
                       LocalDateTime start, LocalDateTime end) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    // Getters
    public int getAppointmentId() { return appointmentId; }
    public int getCustomerId() { return customerId; }
    public int getUserId() { return userId; }
    public int getContactId() { return contactId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    // Setters
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setContactId(int contactId) { this.contactId = contactId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}