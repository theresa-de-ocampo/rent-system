
package rentsystem;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theresa De Ocampo
 */
public class NewItemDB {
    private int lastInventoryID;
    private String inventoryCategory;
    private String inventoryName;
    private int inventoryQty;
    private String inventoryImage;
    
    
    public NewItemDB(String category, String name, int qty, String photoDir){
        inventoryCategory = category;
        inventoryName = name;
        inventoryQty = qty;
        inventoryImage = photoDir;
        
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            lastInventoryID = getLastInventoryID(c);    // Gets the last inventoryID of the set category
            adjustInventoryPrimaryKeys(c);
            insertNewItem(c);
        } 
        catch (SQLException ex) {
            Logger.getLogger(NewItemDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private int getLastInventoryID(NewConnection c) throws SQLException{
        c.ps = c.con.prepareStatement("SELECT LAST(inventoryID) AS lastInventoryID FROM (SELECT inventoryID FROM inventory WHERE inventoryCategory = ?)");
        c.ps.setString(1, inventoryCategory);
        c.rs = c.ps.executeQuery();
        if (c.rs.next())
            return c.rs.getInt("lastInventoryID");
        return 0;
    }
    
    private void adjustInventoryPrimaryKeys(NewConnection c) throws SQLException{
        /* The primary key constraint of inventory table should be temporarily
         *     disabled here before adding a new record. But MS Access does not 
         *     support DDL. This system resorted to the kludgy solution of not 
         *     implementing a primary key constraint to inventory table. The 
         *     constraint is therfore purely handled by this Java program, 
         *     not using the database software.
         */
        c.ps = c.con.prepareStatement("SELECT * FROM inventory WHERE inventoryID > " + lastInventoryID);
        c.rs = c.ps.executeQuery();
        String currentItem;
        int newKey = lastInventoryID + 2;
        while (c.rs.next()){
            currentItem = c.rs.getString("inventoryName");
            c.ps = c.con.prepareStatement("UPDATE inventory SET inventoryID = ? WHERE inventoryName = ?");
            c.ps.setInt(1, newKey);
            c.ps.setString(2, currentItem);
            c.ps.executeUpdate();
            newKey += 1;
        }
        // The primary key constraint should have then been reenabled at this point.
    }
    
    private void insertNewItem(NewConnection c) throws SQLException{
        c.ps = c.con.prepareStatement("INSERT INTO inventory (inventoryID, inventoryCategory, inventoryName, inventoryQty, inventoryImage) VALUES (?, ?, ?, ?, ?)");
        c.ps.setInt(1, lastInventoryID + 1);
        c.ps.setString(2, inventoryCategory);
        c.ps.setString(3, inventoryName);
        c.ps.setInt(4, inventoryQty);
        c.ps.setString(5, inventoryImage);
        c.ps.executeUpdate();
    }
}