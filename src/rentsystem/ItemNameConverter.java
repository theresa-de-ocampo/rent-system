
package rentsystem;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class ItemNameConverter {
    private DBRecords counter = new DBRecords();
    private int n = counter.getN();
    private String[][] dictionary = new String[n][2];
    
    public ItemNameConverter(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT inventoryID, inventoryName FROM inventory");
            c.rs = c.ps.executeQuery();
            
            int r = 0;
            while (c.rs.next()){
                dictionary[r][0] = c.rs.getString("inventoryID");
                dictionary[r++][1] = c.rs.getString("inventoryName");
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(ItemNameConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    public int getItemID(String itemName){
        for (int r = 0; r < n; ++r)
            if (itemName.toLowerCase().equals(dictionary[r][1]))
                return Integer.parseInt(dictionary[r][0]);
        return 0;
    }
}