package rentsystem;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author Theresa De Ocampo
 */
public class RentSystem extends JFrame{
    private class HomeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (!(rentDetailIsRunning())){
                Main.inventoryButton.setBackground(Style.BLUEGREEN);
                Main.pickUpDatesButton.setBackground(Style.BLUEGREEN);
                Main.homeButton.setBackground(Style.AQUA);
                CustomerDetails customerInterface = new CustomerDetails();
                JPanel customerPanel = customerInterface.getPanel();
                Main.contentPanel.removeAll();
                Main.contentPanel.add(customerPanel);
                Main.contentPanel.revalidate();
           }
        }
    }
    
    private class InventoryListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (!(rentDetailIsRunning())){
                Main.homeButton.setBackground(Style.BLUEGREEN);
                Main.pickUpDatesButton.setBackground(Style.BLUEGREEN);
                Main.inventoryButton.setBackground(Style.AQUA);
                Inventory inventoryInterface = new Inventory();
                Main.contentPanel.removeAll();
                Main.contentPanel.add(inventoryInterface);
                Main.contentPanel.revalidate();
            }
        }
    }
    
    private class PickUpDateListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (!(rentDetailIsRunning())){
                Main.homeButton.setBackground(Style.BLUEGREEN);
                Main.inventoryButton.setBackground(Style.BLUEGREEN);
                Main.pickUpDatesButton.setBackground(Style.AQUA);
                Main.contentPanel.removeAll();
                PickUpDate pickUpDateInterface = new PickUpDate();
                JScrollPane tablesScrollPane = new JScrollPane(pickUpDateInterface);
                tablesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                tablesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                Main.contentPanel.add(tablesScrollPane);
                Main.contentPanel.revalidate();
            }
        }
    }
    
    private boolean rentDetailIsRunning(){
        if (RentDetails.custID != 0){
            Style.initJOptionPane();
            JOptionPane.showMessageDialog(null, "You haven't saved your record yet.\nPlease press \"Done\" or \"Cancel.\"", "Andrion", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }
    
    public RentSystem(){
        super("Andrion");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Style.BLUEGREEN);
        
        JLabel headerLabel = new JLabel("Minda Andrion's Tracking System");
        headerLabel.setFont(new Font("Elephant", Font.BOLD, 60));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(headerLabel);
        
        Main.contentPanel.setBackground(Style.AQUA);
        Main.contentPanel.setLayout(new BorderLayout());
        
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setBackground(Style.AQUA);

        Insets mainButtonsMargin = new Insets(10, 180, 10, 180);
        Main.homeButton.setBackground(Style.BLUEGREEN);
        Main.homeButton.setMargin(mainButtonsMargin);
        Main.homeButton.setFont(Style.BODY_FONT);
        Main.homeButton.addActionListener(new HomeListener());
        
        Main.inventoryButton.setBackground(Style.BLUEGREEN);
        Main.inventoryButton.setMargin(mainButtonsMargin);
        Main.inventoryButton.setFont(Style.BODY_FONT);
        Main.inventoryButton.addActionListener(new InventoryListener());
        
        Main.pickUpDatesButton.setBackground(Style.BLUEGREEN);
        Main.pickUpDatesButton.setMargin(mainButtonsMargin);
        Main.pickUpDatesButton.setFont(Style.BODY_FONT);
        Main.pickUpDatesButton.addActionListener(new PickUpDateListener());
        
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(Main.homeButton);
        mainMenuBar.add(Main.inventoryButton);
        mainMenuBar.add(Main.pickUpDatesButton);
        
        mainMenuPanel.add(mainMenuBar);
        headerPanel.add(mainMenuPanel);
        add(headerPanel, BorderLayout.NORTH);
        add(Main.contentPanel, BorderLayout.CENTER);
        Main.homeButton.doClick();
        
        this.addWindowListener( new WindowAdapter() {
            @Override
            public void windowOpened( WindowEvent e ){
                CustomerDetails.getInitialField().requestFocus();
            }
        });
    }
    
    public static void addEmptyRow(JPanel p, int maxColumns){
        for (int c = 0; c < maxColumns; ++c)
            p.add(new JPanel());
    }
}