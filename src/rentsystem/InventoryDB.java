
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JOptionPane;
/**
 *
 * @author Kaye Ann Javier
 * @author Guian Angelo Salamat
 */
public class InventoryDB extends JPanel{
    private final NewConnection c = new NewConnection();
    private String category;

    public InventoryDB(){
        setLayout(new GridLayout(0, 1, -5, -5));
        c.openConnection();
    }
    
    public void displayChoices(){
        try{
            c.ps = c.con.prepareStatement("SELECT inventoryName, inventoryQty, inventoryImage FROM inventory WHERE inventoryCategory LIKE ?");
            c.ps.setString(1, category);
            c.rs = c.ps.executeQuery();
            
            while(c.rs.next()){
            	JPanel cPanel = new JPanel();
            	cPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            	
                JPanel space = new JPanel();
            	space.setLayout(new FlowLayout(FlowLayout.CENTER));
            	space.setPreferredSize(new Dimension(10, 10));
                
            	JPanel inventoryPanel = new JPanel();
            	inventoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            	inventoryPanel.setPreferredSize(new Dimension(630, 107));
                inventoryPanel.setBorder(Style.THIN_LINE);
            	
            	TitleCase formatter = new TitleCase();
            	JLabel inventoryName = new JLabel(formatter.getTitleCase(c.rs.getString(1)), JLabel.CENTER);
            	inventoryName.setFont(Style.BODY_FONT);
            	
            	ImageIcon inventoryImage = new ImageIcon(c.rs.getString(3));
            	Image scaledImage = inventoryImage.getImage().getScaledInstance(125, 96, Image.SCALE_SMOOTH);
            	
            	JLabel imageHolder = new JLabel(new ImageIcon(scaledImage));
            	imageHolder.setPreferredSize(new Dimension(125, 96));
            	
            	JPanel spinnerPanel = new JPanel();           
            	spinnerPanel.setLayout(new GridBagLayout());
                GridBagConstraints gc = new GridBagConstraints();
                gc.insets = new Insets(0, 10, 0, 10);
            	spinnerPanel.setPreferredSize(new Dimension(345, 107));
                spinnerPanel.setBorder(Style.THIN_LINE);
            	
            	JSpinner quantityField = SpinnerControl.createCustomJSpinner(100, 40);
                quantityField.setValue(c.rs.getInt(2));
                int oldQuantity = c.rs.getInt(2);
                
                JButton btnUpdate = Style.createButton("Update");
                btnUpdate.setPreferredSize(new Dimension(120, 40));
                btnUpdate.addActionListener((ActionEvent e) -> {
                    NewConnection cb = new NewConnection();
                        try {
                            cb.openConnection();
                            cb.ps = cb.con.prepareStatement("UPDATE inventory SET inventoryQty = ? WHERE inventoryName LIKE '"+inventoryName.getText()+"'");
                            cb.ps.setInt(1, (int) quantityField.getValue());
                            cb.ps.executeUpdate();
                            if (oldQuantity < (int) quantityField.getValue()){
                                Style.initJOptionPane();
                                int answer = JOptionPane.showConfirmDialog(null, "Increase quantity?", "Andrion", JOptionPane.YES_NO_OPTION);
                                if (answer == 0){
                                    Style.initJOptionPane();
                                    JOptionPane.showMessageDialog(null, "Update was successful!", "Andrion", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            else {
                                Style.initJOptionPane();
                                int answer = JOptionPane.showConfirmDialog(null, "Decrease quantity?", "Andrion", JOptionPane.YES_NO_OPTION);
                                if (answer == 0){
                                    int newQty = oldQuantity - (int) quantityField.getValue();
                                    InventoryReport reporter = new InventoryReport(inventoryName.getText(), newQty);
                                    reporter.setVisible(true);
                                }
                            }
                        }
                        catch (SQLException ex) {
                            Logger.getLogger(InventoryDB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        finally{
                            cb.closeConnection();
                        }
                    });
                   
                inventoryPanel.add(imageHolder, BorderLayout.WEST);
                inventoryPanel.add(inventoryName, BorderLayout.CENTER);
                spinnerPanel.add(quantityField, gc);
                spinnerPanel.add(btnUpdate, gc);
                cPanel.add(inventoryPanel);
                cPanel.add(spinnerPanel);
                add(cPanel);
            }
        }   
        catch (SQLException ex) {
                Logger.getLogger(InventoryDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    public void setCategory(String category){
        this.category = category;
        displayChoices();
    }
}