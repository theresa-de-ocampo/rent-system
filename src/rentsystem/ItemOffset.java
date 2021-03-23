
package rentsystem;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class ItemOffset {
    public static final String BUFFET_LINE_CATEGORY = "Buffet Line";
    public static final String FURNITURE_CATEGORY = "Dining Furniture";
    public static final String FLATWARE_CATEGORY = "Flatware";
    public static final String CARRIER_CATEGORY = "Food & Beverage Carrier";

    public int getOffset(String category) {
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT FIRST(inventoryID) AS firstInventoryID FROM (SELECT * FROM inventory WHERE inventoryCategory =?)");
            c.ps.setString(1, category);
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                return c.rs.getInt("firstInventoryID");
        } 
        catch (SQLException ex) {
            Logger.getLogger(ItemOffset.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return 0;
    }
}