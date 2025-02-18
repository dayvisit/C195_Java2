package davis.c195.DAO;

import davis.c195.helpers.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import davis.c195.model.Country;
import davis.c195.model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object class for location-related database operations
 */
public class LocationDAO {

    /**
     * Gets all countries from the database
     * @return ObservableList of Country objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<Country> getAllCountries() throws SQLException {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country FROM countries";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Country country = new Country(
                        rs.getInt("Country_ID"),
                        rs.getString("Country")
                );
                countries.add(country);
            }
        }
        return countries;
    }

    /**
     * Gets all divisions from the database
     * @return ObservableList of FirstLevelDivision objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions() throws SQLException {
        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
        String sql = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FirstLevelDivision division = new FirstLevelDivision(
                        rs.getInt("Division_ID"),
                        rs.getString("Division"),
                        rs.getInt("Country_ID")
                );
                divisions.add(division);
            }
        }
        return divisions;
    }

    /**
     * Gets divisions for a specific country
     * @param countryId country ID to filter by
     * @return ObservableList of FirstLevelDivision objects
     * @throws SQLException if database error occurs
     */
    public static ObservableList<FirstLevelDivision> getDivisionsByCountry(int countryId) throws SQLException {
        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
        String sql = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions WHERE Country_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FirstLevelDivision division = new FirstLevelDivision(
                        rs.getInt("Division_ID"),
                        rs.getString("Division"),
                        rs.getInt("Country_ID")
                );
                divisions.add(division);
            }
        }
        return divisions;
    }

    /**
     * Gets a country by its ID
     * @param countryId ID to search for
     * @return Country object if found, null if not
     * @throws SQLException if database error occurs
     */
    public static Country getCountry(int countryId) throws SQLException {
        String sql = "SELECT Country_ID, Country FROM countries WHERE Country_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Country(
                        rs.getInt("Country_ID"),
                        rs.getString("Country")
                );
            }
        }
        return null;
    }

    /**
     * Gets a division by its ID
     * @param divisionId ID to search for
     * @return FirstLevelDivision object if found, null if not
     * @throws SQLException if database error occurs
     */
    public static FirstLevelDivision getDivision(int divisionId) throws SQLException {
        String sql = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions WHERE Division_ID = ?";

        try (PreparedStatement ps = DBConnection.connection.prepareStatement(sql)) {
            ps.setInt(1, divisionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new FirstLevelDivision(
                        rs.getInt("Division_ID"),
                        rs.getString("Division"),
                        rs.getInt("Country_ID")
                );
            }
        }
        return null;
    }
}