package davis.c195.model;

import java.time.LocalDateTime;

/**
 * Represents a country in the scheduling system.
 * Maps to the COUNTRIES table in the database.
 */
public class Country {
    private int countryID;
    private String country;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;

    /**
     * Country class constructor with essential fields
     *
     * @param countryID id
     * @param country name
     */
    public Country(int countryID, String country) {
        this.countryID = countryID;
        this.country = country;
    }

    /**
     * Full constructor with all fields
     *
     * @param countryID id
     * @param country name
     * @param createDate creation date
     * @param createdBy creator
     * @param lastUpdate last update time
     * @param lastUpdatedBy last updater
     */
    public Country(int countryID, String country, LocalDateTime createDate,
                   String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy) {
        this.countryID = countryID;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return countryID id
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * @param countryID id
     */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    /**
     * @return country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return createDate creation date
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate creation date
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * @return createdBy creator
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy creator
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return lastUpdate last update time
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate last update time
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return lastUpdatedBy last updater
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy last updater
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}