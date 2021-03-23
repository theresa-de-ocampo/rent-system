
package rentsystem;

import java.sql.*;

/**
 *
 * @author Theresa De Ocampo
 */
public class CustomerDetailsDB {
    private int custID;
    
    public CustomerDetailsDB(Customer client){
        NewConnection c = new NewConnection();
        c.openConnection();
        try{
            java.sql.Date sqlRentDate = new java.sql.Date(client.getRentDate().getTime());
            java.sql.Date sqlReturnDate = new java.sql.Date(client.getReturnDate().getTime());
            c.ps = c.con.prepareStatement(
                "INSERT INTO custDetail (custFName, custLName, custContactNo, custRentDate, custReturnDate, custHouseNo, custStreet, custBarangay, custCityOrTown, custProvince, custLandmark)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS
            );
            c.ps.setString(1, client.getFName());
            c.ps.setString(2, client.getLName());
            c.ps.setString(3, client.getContactNo());
            c.ps.setDate(4, sqlRentDate);
            c.ps.setDate(5, sqlReturnDate);
            c.ps.setString(6, client.getHouseNo());
            c.ps.setString(7, client.getStreet());
            c.ps.setString(8, client.getBarangay());
            c.ps.setString(9, client.getCityOrTown());
            c.ps.setString(10, client.getProvince());
            c.ps.setString(11, client.getLandmark());
            c.ps.executeUpdate();
            c.rs = c.ps.getGeneratedKeys();
            if (c.rs != null && c.rs.next())
                custID = c.rs.getInt(1);
        }
        catch(SQLException ex){
            System.out.println(ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    public int getCustomerID(){
        return custID;
    }
}