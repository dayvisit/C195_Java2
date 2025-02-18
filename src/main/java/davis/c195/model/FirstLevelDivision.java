package davis.c195.model;

/**
 * Represents a first-level division in the scheduling system.
 * Maps to the FIRST_LEVEL_DIVISIONS table in the database.
 */
public class FirstLevelDivision {
    private int divisionID;
    private String divisionName;
    private int countryID;

    /**
     * FirstLevDivision constructor
     *
     * @param divisionID id
     * @param divisionName name
     * @param countryID country id foreign key
     */
    public FirstLevelDivision(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * @return divisionName name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * @param divisionName name
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * @return divisionID id
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID id
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
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
}