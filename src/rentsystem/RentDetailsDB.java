
package rentsystem;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

/**
 *
 * @author Theresa De Ocampo
 */
public class RentDetailsDB extends JPanel{
    private final NewConnection c = new NewConnection();
    private String category = "Buffet Line";
    private int n, i = 0;
    JSpinner spinners[];

    public RentDetailsDB(){
        setLayout(new GridLayout(0, 2));
    }
    
    public void displayChoices(){
        try{
            countRecords();
            createSpinners();
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT inventoryName, inventoryQty, inventoryImage FROM inventory WHERE inventoryCategory = ?");
            c.ps.setString(1, category);
            c.rs = c.ps.executeQuery();
            
            while(c.rs.next()){
                // Set the panels
                JPanel itemPanelHolder = new JPanel();
                JPanel itemPanel = new JPanel();
                JPanel componentPanel = new JPanel();
                
                itemPanelHolder.setBorder(Style.THICK_LINE);
                itemPanel.setLayout(new FlowLayout());
                itemPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
                
                componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
                componentPanel.setPreferredSize(new Dimension(200, 128));
                
                // Set the first component of itemPanel - itemImage
                ImageIcon itemImage = new ImageIcon(c.rs.getString(3));
                Image scaledImage = itemImage.getImage().getScaledInstance(125, 128, Image.SCALE_SMOOTH);
                JLabel imageHolder = new JLabel(new ImageIcon(scaledImage));
                
                // Set Contents of componentPanel - itemName & JSpinner
                TitleCase formatter = new TitleCase();
                JLabel itemName = new JLabel(formatter.getTitleCase(c.rs.getString(1)), SwingConstants.CENTER);
                itemName.setPreferredSize(new Dimension(500, 50));
                itemName.setMaximumSize(new Dimension(500, 50));
                itemName.setMinimumSize(new Dimension(500, 50));
                itemName.setFont(Style.BODY_FONT);
                itemName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                
                Dimension d = spinners[i].getPreferredSize();
                d.width = 60;
                d.height = 30;
                
                // Add components
                componentPanel.add(itemName);
                componentPanel.add(spinners[i++]);
                
                itemPanelHolder.setBackground(Style.BLUEGREEN);
                itemPanelHolder.add(imageHolder);
                itemPanelHolder.add(componentPanel);
                
                itemPanel.add(itemPanelHolder);
                add(itemPanel);
            }
        }   
        catch (SQLException ex) {
                Logger.getLogger(RentDetailsDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    public void setCategory(String category){
        this.category = category;
    }
    
    public void countRecords(){
       NewConnection c = new NewConnection();
       c.openConnection();
        try {
            c.ps = c.con.prepareStatement("SELECT COUNT(inventoryCategory) n FROM inventory WHERE inventoryCategory = ?");
            c.ps.setString(1, category);
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                n = Integer.parseInt(c.rs.getString("n"));
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetailsDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    private void createSpinners(){
        spinners = new JSpinner[n];
        for (int ctr = 0; ctr < n; ++ctr)
            spinners[ctr] = SpinnerControl.createCustomJSpinner(100, 60);
    }
    
    public String getCategory(){
        return category;
    }
    
    public int getN(){
        return n;
    }
}