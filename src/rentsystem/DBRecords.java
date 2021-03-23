
package rentsystem;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class DBRecords {
    private static final int ERROR_FLAG = -1;
    
    public int getN(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT COUNT(*) n FROM inventory");
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                return Integer.parseInt(c.rs.getString("n"));
        } 
        catch (SQLException ex) {
            Logger.getLogger(DBRecords.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
        return ERROR_FLAG;
    }
    
    public int getLastAvailabilityID(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT LAST (availabilityID) lastAvailabilityID FROM availabilityDetail");
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                return (int) Math.round(Double.parseDouble(c.rs.getString("lastAvailabilityID")));
        } 
        catch (SQLException ex) {
            Logger.getLogger(DBRecords.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return ERROR_FLAG;
    }
}