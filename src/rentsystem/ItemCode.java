
package rentsystem;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class ItemCode {
    private String inventoryName;
    private String sql = "SELECT inventoryName from inventory WHERE inventoryID = ";
    private boolean flag = true;
    private ItemOffset io = new ItemOffset();
    
    public ItemCode(String category, int i){
        switch(category){
            case ItemOffset.BUFFET_LINE_CATEGORY:
                sql += i;
                break;
            case ItemOffset.FURNITURE_CATEGORY:
                sql += (i + io.getOffset(ItemOffset.FURNITURE_CATEGORY)) - 1;
                break;
            case ItemOffset.FLATWARE_CATEGORY:
                sql += (i + io.getOffset(ItemOffset.FLATWARE_CATEGORY)) - 1;
                break;
            case ItemOffset.CARRIER_CATEGORY:
                sql += (i + io.getOffset(ItemOffset.CARRIER_CATEGORY)) - 1;
                break;
            default:
                System.out.println("An unexpected error occurred at ItemCode.");
                flag = false;
        }
        if (flag)
            executeSQL(sql);
    }
    
    public String getItemName(){
        return inventoryName;
    }
    
    public void executeSQL(String sql){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement(sql);
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                inventoryName = c.rs.getString("inventoryName");
        } 
        catch (SQLException ex) {
            Logger.getLogger(ItemCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
}