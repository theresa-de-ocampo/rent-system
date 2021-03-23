
package rentsystem;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class NewConnection {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    public void openConnection(){
        String url = "External Files//MARent.accdb";
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String driver = "jdbc:ucanaccess://" + url;
            con = DriverManager.getConnection(driver);
        }
        catch(SQLException ex){
            System.out.println(ex);
        }   
        catch (ClassNotFoundException ex) {
                Logger.getLogger(NewConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection(){
        try {
            if (con != null)
                con.close();
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } 
        catch (SQLException ex) {
            Logger.getLogger(NewConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}