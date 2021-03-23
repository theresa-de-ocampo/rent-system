
package rentsystem;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author Theresa De Ocampo
 */
public class SideMenu {
    JButton buffetLineButton = createButton("Buffet Line");
    JButton diningFurnitureButton = createButton("Dining Furniture");
    JButton flatwareButton = createButton("Flatware");
    JButton carrierButton = createButton("Food & Beverage Carrier");
    private final JTextField searchField = Style.createTextField(11);
    private JButton searchButton = new JButton();
    private String where;
    private String category;
    private JTextField cartTextField = new JTextField(5);
    private int cartValue = 0;
    
    public class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            String itemTarget = searchField.getText();
            itemTarget = itemTarget.toLowerCase();
            NewConnection c = new NewConnection();
            try {
                c.openConnection();
                c.ps = c.con.prepareStatement("SELECT inventoryCategory FROM inventory WHERE inventoryName = ?");
                c.ps.setString(1, itemTarget);
                c.rs = c.ps.executeQuery();
                if (c.rs.next()){
                    if (where.equals("Rent Details"))
                        RentDetails.relayoutChoicesScrollPane(c.rs.getString("inventoryCategory"));
                    else
                        Inventory.relayoutChoicesScrollPane(c.rs.getString("inventoryCategory"));
                    
                    switch(c.rs.getString("inventoryCategory")){
                        case ItemOffset.BUFFET_LINE_CATEGORY:
                            activateBuffetLine();
                            break;
                        case ItemOffset.FURNITURE_CATEGORY:
                            activateDiningFurniture();
                            break;
                        case ItemOffset.FLATWARE_CATEGORY:
                            activateFlatware();
                            break;
                        case ItemOffset.CARRIER_CATEGORY:
                            activateCarrier();
                            break;
                        default:
                            System.out.println("An unexpected error occurred at SideMenu -> SearchListener -> actionPerformed.");
                    }
                }
                else{
                    Toolkit.getDefaultToolkit().beep();
                    UIManager.put("OptionPane.messageFont", Style.MONOSPACED_FONT);
                    JOptionPane.showMessageDialog(null, "Sorry, your search didn't return any results.", "Andrion", JOptionPane.INFORMATION_MESSAGE);
                }
            } 
            catch (SQLException ex) {
                Logger.getLogger(SideMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                c.closeConnection();
            }
        }
    }
    
    private class AddItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            NewItem driver = new NewItem(category);
            driver.setVisible(true);
        }
    }
    
    public SideMenu(){
        activateBuffetLine();
    }
    
    public void activateBuffetLine(){ 
        buffetLineButton.setBackground(Style.BLUEGREEN);
        diningFurnitureButton.setBackground(Style.AQUA);
        flatwareButton.setBackground(Style.AQUA);
        carrierButton.setBackground(Style.AQUA);
        category = ItemOffset.BUFFET_LINE_CATEGORY;
    }
    
    public void activateDiningFurniture(){
        buffetLineButton.setBackground(Style.AQUA);
        diningFurnitureButton.setBackground(Style.BLUEGREEN);
        flatwareButton.setBackground(Style.AQUA);
        carrierButton.setBackground(Style.AQUA);
        category = ItemOffset.FURNITURE_CATEGORY;
    }
    
    public void activateFlatware(){
        buffetLineButton.setBackground(Style.AQUA);
        diningFurnitureButton.setBackground(Style.AQUA);
        flatwareButton.setBackground(Style.BLUEGREEN);
        carrierButton.setBackground(Style.AQUA);
        category = ItemOffset.FLATWARE_CATEGORY;
    }
    
    public void activateCarrier(){
        buffetLineButton.setBackground(Style.AQUA);
        diningFurnitureButton.setBackground(Style.AQUA);
        flatwareButton.setBackground(Style.AQUA);
        carrierButton.setBackground(Style.BLUEGREEN);
        category = ItemOffset.CARRIER_CATEGORY;
    }
    
    public JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayout(4, 1));
        menuBar.add(buffetLineButton);
        menuBar.add(diningFurnitureButton);
        menuBar.add(flatwareButton);
        menuBar.add(carrierButton);
        menuBar.setBackground(Style.BLUEGREEN);
        return menuBar;
    }
    
    public JPanel createSearchPanel(String where){
        this.where = where;
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setBackground(Style.SMOKE);
        searchPanel.setBorder(Style.THIN_LINE);
        
        Image searchIcon;
        JLabel imageLabel = null;
        try {
            searchIcon = ImageIO.read(new File("External Files//Pictures//search.png"));
            imageLabel = new JLabel(new ImageIcon(searchIcon.getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
        } 
        catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        searchButton.add(imageLabel);
        searchButton.setBackground(Style.BLUEGREEN);
        searchButton = Style.setButtonBorder(searchButton, 10);
        searchButton.addActionListener(new SearchListener());
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        return searchPanel;
    }
    
    private JButton createButton(String label){
        JButton b = new JButton(label);
        b.setFont(Style.BODY_FONT);
        return b;
    }
    
    public JButton createAddItemButton(){
        JButton addItem = Style.createHyperlinkButton("+ Add New Item", 270, 5, 5, 5);
        addItem.addActionListener(new AddItemListener());
        return addItem;
    }
    
    public JPanel createCartPanel(){
        JPanel cartPanel = new JPanel();
        cartPanel.setBorder(BorderFactory.createEmptyBorder(220, 5, 5, 5));
        cartPanel.setBackground(Style.BARLEY);
        cartTextField.setText("Cart (0)");
        cartTextField.setFont(Style.BODY_FONT);
        cartTextField.setForeground(Color.RED);
        cartTextField.setBorder(null);
        cartTextField.setEditable(false);
        cartTextField.setOpaque(false);
        cartPanel.add(cartTextField);
        return cartPanel;
    }
    
    public JTextField getCart(){
        return cartTextField;
    }
    
    public void updateCart(int additional){
        cartValue += additional;
        cartTextField.setText("Cart (" + cartValue + ")");
    }
    
    public void resetCartValue(){
        cartValue = 0;
    }
}