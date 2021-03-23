
package rentsystem;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author Theresa De Ocampo
 */
public class RentDetails extends JPanel{
    private final SideMenu sideMenuInterface = new SideMenu();
    private static JPanel sectionPanel = new JPanel();
    private static RentDetailsDB choicesInterface = new RentDetailsDB();
    private static JScrollPane choicesScrollPane = new JScrollPane(choicesInterface);;
    private int[] amounts;
    static int custID = 0;
    private int rentDay, returnDay;
    private final int[][] itemTable;
    private int lastDaySet = 0;
    private final int[][] basis;
    private final int seed_day;
    private final int n;
    private final int[][] lastDayTableBeforeUpdate;
    private int lastDayBeforeUpdate = 0;
    private int alt = 0;
    private PSTConverter pc;
    
    private class BuffetLineListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            relayoutChoicesScrollPane("Buffet Line");
            sideMenuInterface.activateBuffetLine();
        }
    }
    
    private class DiningFurnitureListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            relayoutChoicesScrollPane("Dining Furniture");
            sideMenuInterface.activateDiningFurniture();
        }
    }
    
    private class FlatwareListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            relayoutChoicesScrollPane("Flatware");
            sideMenuInterface.activateFlatware();
        }
    }
    
    private class CarrierListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            relayoutChoicesScrollPane("Food & Beverage Carrier");
            sideMenuInterface.activateCarrier();
        }
    }
    
    public static void relayoutChoicesScrollPane(String category){
        if (choicesScrollPane != null)
            sectionPanel.remove(choicesScrollPane);
        choicesInterface = new RentDetailsDB();
        choicesInterface.setCategory(category);
        choicesInterface.displayChoices();
        choicesScrollPane = new JScrollPane(choicesInterface);
        choicesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        choicesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sectionPanel.add(choicesScrollPane, BorderLayout.CENTER);
        sectionPanel.validate();
    }
    
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            String[] options = {"Yes", "No"};
            Style.initJOptionPane();
            int answer = JOptionPane.showOptionDialog(
                null, 
                "Do you want to cancel?\nAll content will be lost.", 
                "Andrion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
            if (answer == 0){
                NewConnection c = new NewConnection();
                try {
                    c.openConnection();
                    c.ps = c.con.prepareStatement("DELETE FROM rentDetail WHERE custID = ?");
                    c.ps.setString(1, String.valueOf(custID));
                    c.ps.executeUpdate();
                    
                    c.ps = c.con.prepareStatement("DELETE FROM custDetail WHERE custID = ?");
                    c.ps.setString(1, String.valueOf(custID));
                    c.ps.executeUpdate();
                } 
                catch (SQLException ex) {
                    Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    c.closeConnection();
                }
                custID = 0;
                Main.contentPanel.removeAll();
                Main.homeButton.doClick();
            }
        }
    }
    
    private class DoneListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            prepareForNextRent();
            Summary summaryWindow = new Summary(custID, pc);
            summaryWindow.setVisible(true);
            sideMenuInterface.resetCartValue();
        }
    }
    
    private class AddListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            int n = choicesInterface.getN();
            int iterative_flag, flag = 0;
            String category = choicesInterface.getCategory();
            setAmounts();
            String message = "Sorry, the following item(s) seems to be running low.\n\n";
            ChangeableString messageMaker = new ChangeableString(message);
            
            for (int i = 0; i < n; ++i){
                if (amounts[i] != 0){
                    ItemCode translator = new ItemCode(category, i + 1);
                    String rentName = translator.getItemName();
                    AbsoluteItemID itemIDGetter = new AbsoluteItemID(category, i);
                    iterative_flag = checkAvailability(itemIDGetter.getAbsoluteItemID(), amounts[i], rentName, messageMaker);
                    if (iterative_flag != 0)
                        flag = -1;
                    else{
                        addRecord(custID, rentName, amounts[i]);
                        giveFeedback(amounts[i]);
                        updateAvailability(itemIDGetter.getAbsoluteItemID(), amounts[i]);
                    }
                }
            }
            if (flag != 0){
                Style.initJOptionPane();
                JOptionPane.showMessageDialog(null, messageMaker.toString(), "Out of Stock!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setAmounts(){
        int n = choicesInterface.getN();
        amounts = new int[n];
        for (int i = 0; i < n; ++i){
            try {
                choicesInterface.spinners[i].commitEdit();
                amounts[i] = (int) choicesInterface.spinners[i].getValue();
            } 
            catch (ParseException ex) {
                Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private int checkAvailability(int itemID, int qtyToRent, String rentName, ChangeableString messageMaker){
        NewConnection c = new NewConnection();
        TitleCase formatter = new TitleCase();
        int flag = 0;
        
        c.openConnection();
        copyLastDayDetail(c);

        // Seeding the database up to rentDay, but not yet updating the rentDay
        while (rentDay > lastDaySet){
            pasteLastDayDetail(c);
        }

        setItemTable(c, returnDay);
        int flag_returnDay = checkQtyToRent(qtyToRent, itemID, rentName, formatter, messageMaker);
        setItemTable(c, rentDay);
        int flag_rentDay = checkQtyToRent(qtyToRent, itemID, rentName, formatter, messageMaker);
        if (flag_rentDay == -1 || flag_returnDay == -1)
            flag = -1;
        c.closeConnection();
        return flag;
    }
    
    private void addRecord(int custID, String rentName, int rentQty){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT rentQty from rentDetail WHERE custID = ? AND rentName = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.ps.setString(2, rentName);
            c.rs = c.ps.executeQuery();
            
            if(c.rs.next()){
                String previousRentQty = c.rs.getString("rentQty");
                int newRentQty = Integer.parseInt(previousRentQty) + rentQty;
                c.ps = c.con.prepareStatement("UPDATE rentDetail SET rentQty = ? WHERE custID = ? AND rentName = ?");
                c.ps.setString(1, String.valueOf(newRentQty));
                c.ps.setString(2, String.valueOf(custID));
                c.ps.setString(3, rentName);
                c.ps.executeUpdate();
            }
            else{
                c.ps = c.con.prepareStatement("INSERT INTO rentDetail (custID, rentName, rentQty) VALUES(?, ?, ?)");
                c.ps.setString(1, String.valueOf(custID));
                c.ps.setString(2, rentName);
                c.ps.setString(3, String.valueOf(rentQty));
                c.ps.executeUpdate();
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    private void giveFeedback(int rentQty){
        sideMenuInterface.updateCart(rentQty);
        playBeep();
    }
    
    @SuppressWarnings("null")
    private void playBeep(){
        AudioInputStream audioIn = null;
        try {
            File f = new File("External Files//Item Added to Cart Sound.wav");
            audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } 
        catch (MalformedURLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            try {
                audioIn.close();
            } 
            catch (IOException ex) {
                Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void updateAvailability(int itemID, int qtyToRent){
        NewConnection c = new NewConnection();
        c.openConnection();
        int newItemQty = itemTable[itemID - 1][1] - qtyToRent;
        // Variables with suffixes of Alt handles the situation when a set of
        // items were returned by another customer on the rentDay
        int newItemQtyAlt = lastDayTableBeforeUpdate[itemID - 1][1] - qtyToRent;
        // Update rentDay detail
        updateItemQty(c, newItemQty, itemID, rentDay);
        copyLastDayDetail(c);
        
        // Copy the updated rentDay detail all the way to returnDay
        // But if the user chose another category, this wouldn't be enough
        // That's why we have another if block next to it
        while (lastDaySet < returnDay)
            pasteLastDayDetail(c);
        
        // The condition rentDay < lastDaySet handles the situation where
        // another cutomer have a rentDay of way earlier than lastDaySet
        if (rentDay < lastDaySet || lastDaySet >= returnDay) {
            int toBeUpdated = rentDay + 1;
            while (toBeUpdated <= returnDay){
                if (toBeUpdated >= lastDayBeforeUpdate){
                    updateItemQty(c, newItemQtyAlt, itemID, toBeUpdated);
                    alt = 1;
                }
                else
                    updateItemQty(c, newItemQty, itemID, toBeUpdated);
                ++toBeUpdated;
            }
        }
        c.closeConnection();
    }
    
    private void copyLastDayDetail(NewConnection c){
        DBRecords counter = new DBRecords();
        lastDaySet = counter.getLastAvailabilityID();
        setItemTable(c, lastDaySet);
    }
    
    private void pasteLastDayDetail(NewConnection c){
        lastDaySet += 1;
        addADay(c, lastDaySet);
    }
    
    private void updateItemQty(NewConnection c, int newItemQty, int itemID, int availabilityID){
        try {
            c.ps = c.con.prepareStatement("UPDATE item SET itemQty = ? WHERE itemID = ? AND availabilityID = ?");
            c.ps.setString(1, String.valueOf(newItemQty));
            c.ps.setString(2, String.valueOf(itemID));
            c.ps.setString(3, String.valueOf(availabilityID));
            c.ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void seedAvailabilityDetail(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT COUNT(*) days FROM availabilityDetail");
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                if (Integer.parseInt(c.rs.getString("days")) == 0){
                    c.ps = c.con.prepareStatement("INSERT INTO availabilityDetail (availabilityID) VALUES (?)");
                    c.ps.setString(1, String.valueOf(seed_day));
                    c.ps.executeUpdate();
                    
                    c.ps = c.con.prepareStatement("SELECT * FROM inventory");
                    c.rs = c.ps.executeQuery();
                    while (c.rs.next()){
                        c.ps = c.con.prepareStatement("INSERT INTO item (itemID, availabilityID, itemQty) VALUES(?, ?, ?)");
                        c.ps.setString(1, c.rs.getString("inventoryID"));
                        c.ps.setString(2, String.valueOf(seed_day));
                        c.ps.setString(3, c.rs.getString("inventoryQty"));
                        c.ps.executeUpdate();
                    }
                }
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private void setItemTable(NewConnection c, int nth_day){
        int itemID;
        try {
            c.ps = c.con.prepareStatement("SELECT * FROM item WHERE availabilityID = ?");
            c.ps.setString(1, String.valueOf(nth_day));
            c.rs = c.ps.executeQuery();
            while (c.rs.next()){
                itemID = Integer.parseInt(c.rs.getString("itemID"));
                itemTable[itemID - 1][0] = itemID;
                itemTable[itemID - 1][1] = Integer.parseInt(c.rs.getString("itemQty"));
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int checkQtyToRent(int qtyToRent, int itemID, String rentName, TitleCase formatter, ChangeableString messageMaker){
        if (qtyToRent > itemTable[itemID - 1][1]){
            String unavailableItem = String.format("     %1$-20s\n", formatter.getTitleCase(rentName));
            if (!(messageMaker.toString().contains(unavailableItem)))
                messageMaker.adjoin(unavailableItem);
            return -1;
        }
        else
            return 0;
    }
    
    private void prepareForNextRent(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            
            c.ps = c.con.prepareStatement("SELECT * FROM availabilityDetail WHERE availabilityID = ?");
            c.ps.setString(1, String.valueOf(returnDay + 1));
            c.rs = c.ps.executeQuery();
            
            if(!(c.rs.next())){
                c.ps = c.con.prepareStatement("INSERT INTO availabilityDetail (availabilityID) VALUES(?)");
                c.ps.setString(1, String.valueOf(returnDay + 1));
                c.ps.executeUpdate();
                if (alt == 0)
                    setNewLastDay(c, basis);
                else
                    setNewLastDay(c, lastDayTableBeforeUpdate);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    private void setNewLastDay(NewConnection c, int[][] tableToUse){
        for (int i = 0; i < n; ++i){
            try {
                c.ps = c.con.prepareStatement("INSERT INTO item (itemID, availabilityID, itemQty) VALUES(?, ?, ?)");
                c.ps.setString(1,String.valueOf(tableToUse[i][0]));
                c.ps.setString(2, String.valueOf(returnDay + 1));
                c.ps.setString(3, String.valueOf(tableToUse[i][1]));
                c.ps.executeUpdate();
            } 
            catch (SQLException ex) {
                Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void setBasis(){
        int itemID, availabilityID;
        NewConnection c = new NewConnection();
        
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT * FROM availabilityDetail WHERE availabilityID = ?");
            c.ps.setString(1, String.valueOf(rentDay));
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                availabilityID = rentDay;
            else
                availabilityID = seed_day;
            
            
            c.ps = c.con.prepareStatement("SELECT * FROM item WHERE availabilityID = ?");
            c.ps.setString(1, String.valueOf(availabilityID));
            c.rs = c.ps.executeQuery();
            while (c.rs.next()){
                itemID = Integer.parseInt(c.rs.getString("itemID"));
                basis[itemID - 1][0] = itemID;
                basis[itemID - 1][1] = Integer.parseInt(c.rs.getString("itemQty"));
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    private void initializeDays(){
        NewConnection c = new NewConnection();
        try {
            String custRentDate = "", custReturnDate= "";
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT * FROM custDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();
            if(c.rs.next()){
                custRentDate = c.rs.getString("custRentDate");
                custReturnDate = c.rs.getString("custReturnDate");
            }   
            DateConverter dc = new DateConverter();
            rentDay = dc.getNthDay(custRentDate);
            returnDay = dc.getNthDay(custReturnDate);
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            c.closeConnection();
        }
    }
    
    private void addADay(NewConnection c, int availabilityID){
        try {
            c.ps = c.con.prepareStatement("INSERT INTO availabilityDetail (availabilityID) VALUES(?)");
            c.ps.setString(1, String.valueOf(availabilityID));
            c.ps.executeUpdate();
            for (int i = 0; i < n; ++i){
                c.ps = c.con.prepareStatement("INSERT INTO item (itemID, availabilityID, itemQty) VALUES(?, ?, ?)");
                c.ps.setString(1,String.valueOf(itemTable[i][0]));
                c.ps.setString(2, String.valueOf(availabilityID));
                c.ps.setString(3, String.valueOf(itemTable[i][1]));
                c.ps.executeUpdate();
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getLastDayTable(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT LAST (availabilityID) lastDaySet FROM availabilityDetail");
            c.rs = c.ps.executeQuery();
            
            if (c.rs.next())
                lastDayBeforeUpdate = (int) Math.round(Double.parseDouble(c.rs.getString("lastDaySet")));
            
            c.ps = c.con.prepareStatement("SELECT * FROM item WHERE availabilityID = ?");
            c.ps.setString(1, String.valueOf(lastDayBeforeUpdate));
            c.rs = c.ps.executeQuery();
            int itemID;
            while (c.rs.next()){
                itemID = Integer.parseInt(c.rs.getString("itemID"));
                lastDayTableBeforeUpdate[itemID - 1][0] = itemID;
                lastDayTableBeforeUpdate[itemID - 1][1] = Integer.parseInt(c.rs.getString("itemQty"));
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private int getInstructionFlag(){
        int flag = 0;
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT userInstructionStatus FROM user WHERE userID = 1");
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                flag = c.rs.getInt("userInstructionStatus");
        } 
        catch (SQLException ex) {
            Logger.getLogger(RentDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return flag;
    }
    
    public static void setCustID(int custID){
        RentDetails.custID = custID;
    }
    
    public RentDetails(int custID, PSTConverter pc){
        if (getInstructionFlag() == 0){
            InstructionWindow instructor = new InstructionWindow();
            instructor.setVisible(true);
        }
        DateConverter dc = new DateConverter();
        seed_day = dc.getNthDay() - 1;
        seedAvailabilityDetail();
        DBRecords counter = new DBRecords();
        n = counter.getN();
        itemTable = new int[n][2];
        basis = new int[n][2];
        lastDayTableBeforeUpdate = new int[n][2];
        this.custID = custID;
        this.pc = pc;
        initializeDays();
        setBasis();
        getLastDayTable();
        
        setLayout(new BorderLayout());
        setBackground(Style.AQUA);
        
        JLabel headerLabel = new JLabel("Rent Details");
        headerLabel.setFont(Style.HEADER_FONT);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        headerLabel.setBackground(Style.BLUEGREEN);
        headerLabel.setOpaque(true);
        add(headerLabel, BorderLayout.NORTH);
        
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
        sidePanel.add(sideMenuInterface.createSearchPanel("Rent Details"), BorderLayout.NORTH);
        sidePanel.add(sideMenuInterface.createMenuBar(), BorderLayout.CENTER);
        sidePanel.add(sideMenuInterface.createCartPanel(), BorderLayout.SOUTH);
        menuPanel.add(sidePanel);
     
        sectionPanel.setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Style.BARLEY);
        GridBagLayout gl = new GridBagLayout();
        buttonPanel.setLayout(gl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 0, 50);
        
        JButton cancelButton = Style.createButton("Cancel");
        gl.setConstraints(cancelButton, gbc);
        cancelButton.addActionListener(new CancelListener());
        
        JButton addButton = Style.createButton("Add");
        gl.setConstraints(addButton, gbc);
        addButton.addActionListener(new AddListener());
        
        JButton doneButton = Style.createButton("Done");
        gl.setConstraints(doneButton, gbc);
        doneButton.addActionListener(new DoneListener());
        
        buttonPanel.add(addButton);
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);
        sectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        sideMenuInterface.buffetLineButton.doClick();
       
        contentPanel.add(menuPanel, BorderLayout.WEST);
        contentPanel.add(sectionPanel, BorderLayout.CENTER);
        contentPanel.add(new JPanel(), BorderLayout.SOUTH);
        add(contentPanel);        
    }
}