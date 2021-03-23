
package rentsystem;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author Kaye Ann Javier
 * @author Guian Angelo Salamat
 */
public class Inventory extends JPanel{
    private static JPanel sectionPanel = new JPanel();
    private static JPanel headingPanel = new JPanel();
    private final SideMenu sideMenuInterface = new SideMenu();
    
    private class BuffetLineListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            sideMenuInterface.activateBuffetLine();
            relayoutChoicesScrollPane("Buffet Line");
        }
    }
    
    private class DiningFurnitureListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            sideMenuInterface.activateDiningFurniture();
            relayoutChoicesScrollPane("Dining Furniture");
        }
    }
    
    private class FlatwareListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            sideMenuInterface.activateFlatware();
            relayoutChoicesScrollPane("Flatware");
        }
    }
    
    private class CarrierListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            sideMenuInterface.activateCarrier();
            relayoutChoicesScrollPane("Food & Beverage Carrier");
        }
    }
    
    public static void relayoutChoicesScrollPane(String category){
        InventoryDB choicesInterface = new InventoryDB();
        choicesInterface.setCategory(category);
        sectionPanel.removeAll();
        sectionPanel.add(headingPanel, BorderLayout.NORTH);
        JScrollPane choicesScrollPane = new JScrollPane(choicesInterface);
        choicesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        choicesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sectionPanel.add(choicesScrollPane, BorderLayout.CENTER);
        sectionPanel.validate();
    }
    
    public Inventory(){
        setLayout(new BorderLayout());
        setBackground(Style.AQUA);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setBackground(Style.BARLEY);
        
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Style.BARLEY);
        
        sideMenuInterface.buffetLineButton.addActionListener(new BuffetLineListener());
        sideMenuInterface.diningFurnitureButton.addActionListener(new DiningFurnitureListener());
        sideMenuInterface.flatwareButton.addActionListener(new FlatwareListener());
        sideMenuInterface.carrierButton.addActionListener(new CarrierListener());
        sidePanel.add(sideMenuInterface.createSearchPanel("Inventory"), BorderLayout.NORTH);
        sidePanel.add(sideMenuInterface.createMenuBar(), BorderLayout.CENTER);
        sidePanel.add(sideMenuInterface.createAddItemButton(), BorderLayout.SOUTH);
        menuPanel.add(sidePanel);
        
        sectionPanel.setLayout(new BorderLayout());
        
    	JPanel itemPanel = new JPanel();
        itemPanel.setPreferredSize(new Dimension(630, 50));
        itemPanel.setBorder(Style.THICK_LINE);

    	JLabel lblitem = new JLabel("Items");
    	lblitem.setFont(Style.SUBHEADER_FONT);
    	
    	JPanel quanPanel = new JPanel();
    	quanPanel.setPreferredSize(new Dimension(345, 50));
        quanPanel.setBorder(Style.THICK_LINE);
    	
    	JLabel lblq= new JLabel("Quantity on Hand");
    	lblq.setFont(Style.SUBHEADER_FONT);
    	
    	itemPanel.add(lblitem);
    	quanPanel.add(lblq);
    	
    	headingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	headingPanel.add(itemPanel);
    	headingPanel.add(quanPanel);

        sideMenuInterface.buffetLineButton.doClick();
       
        contentPanel.add(menuPanel, BorderLayout.WEST);
        contentPanel.add(sectionPanel, BorderLayout.CENTER);
        contentPanel.add(new JPanel(), BorderLayout.SOUTH);
        add(contentPanel);        
    }
}