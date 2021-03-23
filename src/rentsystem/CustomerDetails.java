
package rentsystem;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author Theresa De Ocampo
 */
public class CustomerDetails {
    private final JPanel customerPanel = new JPanel();
    private static final int NUMBER_OF_CHAR = 12;
    
    private final JLabel headerLabel = new JLabel("Customer Details");
    private final JPanel namePhonePanelHolder = new JPanel();
    private final JPanel addressPanelHolder = new JPanel();
    private final JPanel reservationPanelHolder = new JPanel();
    private final JPanel buttonPanel = new JPanel();
    
    private final JPanel contentPanel = new JPanel();
    private static final JTextField fNameField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField lNameField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField contactField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField houseNoField = Style.createTextField(3);
    private final JTextField barangayField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField streetField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField cityOrTownField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField provinceField = Style.createTextField(NUMBER_OF_CHAR);
    private final JTextField landmarkField = Style.createTextField(NUMBER_OF_CHAR);
    private final DatePicker rentDateField = new DatePicker();
    private final DatePicker returnDateField = new DatePicker();
    private final JButton nextButton = Style.createButton("Next");
    
    private class NextListener implements ActionListener, KeyListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Customer client = new Customer();
            client.setFName(fNameField.getText());
            client.setLName(lNameField.getText());
            client.setContactNo(contactField.getText());
            client.setRentDate(rentDateField.getDate());
            client.setReturnDate(returnDateField.getDate());
            client.setHouseNo(houseNoField.getText());
            client.setBarangay(barangayField.getText());
            client.setStreet(streetField.getText());
            client.setCityOrTown(cityOrTownField.getText());
            client.setProvince(provinceField.getText());
            client.setLandmark(landmarkField.getText());
            
            PSTConverter pc = new PSTConverter();
            pc.setRentLongDate(String.valueOf(rentDateField.getDate()));
            pc.setReturnLongDate(String.valueOf(returnDateField.getDate()));
            
            boolean isErrorFree = performValidations(client);
            if (isErrorFree){
                CustomerDetailsDB customerRecorder = new CustomerDetailsDB(client);
                int custID = customerRecorder.getCustomerID();

                Main.contentPanel.remove(customerPanel);
                RentDetails rentInterface = new RentDetails(custID, pc);
                Main.contentPanel.add(rentInterface, BorderLayout.CENTER);
                Main.contentPanel.validate();
            }
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                
           }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); 
        }

        @Override
        public void keyReleased(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }
    
    private boolean performValidations(Customer client){
        String errorMessage = "";
        if (client.requiredFieldsAreNotFilled())
            errorMessage += "<html>All fields are required except <b>Province</b> and <b>Landmark</b>";
        else{
            boolean nextLine = false;
            if (client.contactNoIsInvalid()){
                errorMessage += "<html>Please enter a phone number in the format 09XXXXXXXXX";
                nextLine = true;
            }
            if (client.datesAreInvalid())
                if (nextLine)
                    errorMessage += "<br>Invalid dates, return date is past rent date.</br>";
                else
                    errorMessage += "<html>Invalid dates, return date is past rent date.";
        }
            
        if (!(errorMessage.isEmpty())){
            errorMessage += "</html>";
            Style.initJOptionPane();
            JOptionPane.showMessageDialog(null, errorMessage, "Andrion", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else
            return true;
    }
    
    // Sets customerPanel
    public CustomerDetails(){
        customerPanel.setLayout(new BorderLayout());
        customerPanel.setBackground(Style.AQUA);
        
        setHeaderPanelComponents();
        
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        Border margin = new EmptyBorder(10, 10, 10, 10);
        setContentPanelComponents(margin);
        
        buttonPanel.setLayout(new GridBagLayout());
        
        nextButton.setFont(Style.BODY_FONT);
        nextButton.setBackground(Style.BLUEGREEN);
        nextButton.addActionListener(new NextListener());
        addComponents();
    }
    
    private void setHeaderPanelComponents(){
        headerLabel.setFont(Style.HEADER_FONT);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBackground(Style.BLUEGREEN);
        headerLabel.setOpaque(true);
    }
    
    private void setContentPanelComponents(Border margin){
        setNamePhoneSection(margin);
        setAddressSection(margin);
    }
    
    private void setNamePhoneSection(Border margin){
        JPanel namePhonePanel = new JPanel();
        namePhonePanelHolder.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        namePhonePanel.setBorder(new CompoundBorder(Style.createSeparator("Reservation", Style.SUBHEADER_FONT), margin));
        namePhonePanel.setLayout(new BoxLayout(namePhonePanel, BoxLayout.Y_AXIS));
        
        JLabel fNameLabel = Style.createLabel("First Name", 0);
        JLabel lNameLabel = Style.createLabel("Last Name", 50);
        JLabel contactLabel = Style.createLabel("Cellphone Number", 50);
        JLabel rentDateLabel = Style.createLabel("Rent Date", 0);
        JLabel returnDateLabel = Style.createLabel("Return Date", 50);
        
        JPanel firstRowPanel = new JPanel();
        firstRowPanel.add(fNameLabel);
        firstRowPanel.add(fNameField);
        firstRowPanel.add(lNameLabel);
        firstRowPanel.add(lNameField);
        firstRowPanel.add(contactLabel);
        firstRowPanel.add(contactField);
        fNameField.setText("");
        
        JPanel secondRowPanel = new JPanel();
        secondRowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        secondRowPanel.add(rentDateLabel);
        secondRowPanel.add(rentDateField);
        secondRowPanel.add(returnDateLabel);
        secondRowPanel.add(returnDateField);
        
        namePhonePanel.add(firstRowPanel);
        namePhonePanel.add(secondRowPanel);
        namePhonePanelHolder.add(namePhonePanel);     
    }
    
    private void setAddressSection(Border margin){
        JPanel addressPanel = new JPanel();
        addressPanelHolder.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        addressPanel.setBorder(new CompoundBorder(Style.createSeparator("Address", Style.SUBHEADER_FONT), margin));
        addressPanel.setPreferredSize(new Dimension(1160, 200));
        addressPanel.setLayout(new GridLayout(2, 3, 5, 5));
        
        JLabel houseNoLabel = Style.createLabel("House No.", 0);
        JLabel streetLabel = Style.createLabel("Street", 30);
        JLabel barangayLabel = Style.createLabel("Barangay", 0);
        JLabel cityOrTownLabel = Style.createLabel("City/Town", 0);
        JLabel provinceLabel = Style.createLabel("Province", 0);
        JLabel landmarkLabel = Style.createLabel("Landmark", 0);
        
        JPanel houseNoPanel = new JPanel();
        JPanel streetPanel = new JPanel();
        JPanel barangayPanel = new JPanel();
        JPanel cityOrTownPanel = new JPanel();
        JPanel provincePanel = new JPanel();
        JPanel landmarkPanel = new JPanel();
        
        houseNoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        houseNoPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        houseNoPanel.add(houseNoLabel);
        houseNoPanel.add(houseNoField);
        
        streetPanel.add(streetLabel);
        streetPanel.add(streetField);
        
        barangayPanel.add(barangayLabel);
        barangayPanel.add(barangayField);
        
        cityOrTownPanel.add(cityOrTownLabel);
        cityOrTownPanel.add(cityOrTownField);
        
        provincePanel.add(provinceLabel);
        provincePanel.add(provinceField);
        
        landmarkPanel.add(landmarkLabel);
        landmarkPanel.add(landmarkField);
        
        addressPanel.add(houseNoPanel);
        addressPanel.add(streetPanel);
        addressPanel.add(barangayPanel);
        addressPanel.add(cityOrTownPanel);
        addressPanel.add(provincePanel);
        addressPanel.add(landmarkPanel);
        
        addressPanelHolder.add(addressPanel);
        cityOrTownField.addActionListener(new NextListener());
        provinceField.addActionListener(new NextListener());
        landmarkField.addActionListener(new NextListener());
    }
    
    private void addComponents(){
        buttonPanel.add(nextButton, new GridBagConstraints());
        
        contentPanel.add(namePhonePanelHolder);
        contentPanel.add(addressPanelHolder);
        contentPanel.add(reservationPanelHolder);
        contentPanel.add(buttonPanel);
        
        customerPanel.add(headerLabel, BorderLayout.NORTH);
        customerPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    public JPanel getPanel(){
        return customerPanel;
    }
    
    public static JTextField getInitialField(){
        return fNameField;
    }
}