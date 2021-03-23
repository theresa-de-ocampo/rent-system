
package rentsystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
/**
 *
 * @author Theresa De Ocampo
 */
public class IncidentReport extends JFrame {
    private final int custID;
    private final int n;
    private final String[][] tableContent;
    private final JPanel contentPanel = new JPanel();
    private final JPanel commentaryPanel = new JPanel();
    private final JTextArea commentaryField = new JTextArea(3, 18);
    private String custReturnDate;
    private ChangeableString incident = new ChangeableString("");
    private ChangeableString equipment = new ChangeableString("");
    private String custName;
    String incidentCommentary;
    private static final int ERROR_FLAG = 9999;
    
    private class SubmitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            NewConnection c = new NewConnection();
            try {
                c.openConnection();
                submitReport(c);
                Main.pickUpDatesButton.doClick();
            } catch (SQLException ex) {
                Logger.getLogger(IncidentReport.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                c.closeConnection();
            }
        }
    }
    
    private class CloseListener extends WindowAdapter {  
        @Override
        public void windowClosing( WindowEvent e ) {  
            Style.initJOptionPane();
            JOptionPane.showMessageDialog(null, "This report is mandatory!\nPlease press the submit button.", "Andrion", JOptionPane.ERROR_MESSAGE);
        }  
    }  
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public IncidentReport(int custID, int n, String[][] tableContent){
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        addWindowListener(new CloseListener());  
        this.custID = custID;
        this.n = n;
        this.tableContent = new String[n][2];
        initTableContent(tableContent);
        
        JPanel headerPanel = Style.createFormTitlePanel("Equipment Incident Report");
        setContentPanelComponents();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setContentPanelComponents(){
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));
        
        JTextArea messageTextArea = Style.createStaticTextArea(2, 20, "Unreturned items were detected, please submit this report");

        JPanel tablePanel = new JPanel();
        String[] columnTitles = {"Lost Items", "Qty"};
        JTable table = new JTable(tableContent, columnTitles);
        table.setDefaultEditor(Object.class, null);
        JScrollPane tableScrollPane = Style.setFormTableProperties(table);
        tablePanel.add(tableScrollPane);
        
        JPanel instructionsPanel = new JPanel();
        JLabel instructionsLabel = Style.createLabel("Describe the incident.", 0);
        instructionsPanel.add(instructionsLabel);
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        showCommentaryPanel();
        
        JPanel buttonPanel = new JPanel();
        JButton submitButton = Style.createButton("Submit");
        submitButton.addActionListener(new SubmitListener());
        buttonPanel.add(submitButton, new GridBagConstraints());
        
        contentPanel.add(messageTextArea);
        contentPanel.add(tablePanel);
        contentPanel.add(instructionsPanel);
        contentPanel.add(commentaryPanel);
        contentPanel.add(buttonPanel);
    }
    
    public void initTableContent(String[][] tableContent){
        for (int r = 0; r < n; ++r)
            for (int c = 0; c < tableContent[r].length; ++c)
                this.tableContent[r][c] = tableContent[r][c];
    }
    
    private void showCommentaryPanel(){
        commentaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        commentaryField.setFont(Style.FIELD_FONT);
        commentaryField.setBorder(Style.FIELD_MARGIN);
        commentaryField.setLineWrap(true);
        JScrollPane commentaryScrollPane = new JScrollPane(commentaryField);
        commentaryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commentaryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        commentaryPanel.add(commentaryScrollPane);
    }
    
    private void submitReport(NewConnection c) throws SQLException{
        incidentCommentary = commentaryField.getText();
        c.ps = c.con.prepareStatement("INSERT INTO incidentDetail (custID, incidentCommentary) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        c.ps.setInt(1, custID);
        c.ps.setString(2, incidentCommentary);
        c.ps.executeUpdate();
        c.rs = c.ps.getGeneratedKeys();
        int incidentID = 0;
        if (c.rs != null && c.rs.next())
            incidentID = c.rs.getInt(1);

        setCustName(custID, c);
        setReturnDate(custID, c);
        DateConverter dc = new DateConverter();
        int effectiveDay = dc.getNthDay(custReturnDate);
        equipment.adjoin(String.format("    %1$-20s %2$10s<br>", "ITEM", "QUANTITY"));
        
        for (int r = 0; r < n; ++r){
            c.ps = c.con.prepareStatement("INSERT INTO lostItem (incidentID, lostItemName, lostItemQty) VALUES (?, ?, ?)");
            c.ps.setString(1, String.valueOf(incidentID));
            c.ps.setString(2, tableContent[r][0]);
            c.ps.setString(3, tableContent[r][1]);
            c.ps.executeUpdate();

            adjustAvailabilityDetail(tableContent[r][0], Integer.parseInt(tableContent[r][1]), effectiveDay);
            equipment.adjoin(String.format("    %1$-20s %2$8d<br>", tableContent[r][0], Integer.parseInt(tableContent[r][1])));
        }
        setIncidentMessage();
        dispose();
        notification();
        changeCustStatus(c);
   }
    
    private void setCustName(int custID, NewConnection c) throws SQLException{
        c.ps = c.con.prepareStatement("SELECT custFName, custLName FROM custDetail WHERE custID = " + custID);
        c.rs = c.ps.executeQuery();
        if (c.rs.next())
            custName = c.rs.getString("custFName") + " " + c.rs.getString("custLName");
    }
    
    private void setReturnDate(int custID, NewConnection c) throws SQLException{
        c.ps = c.con.prepareStatement("SELECT custReturnDate FROM custDetail WHERE custID = " + custID);
        c.rs = c.ps.executeQuery();
        if (c.rs.next())
            custReturnDate = c.rs.getString("custReturnDate");
    }
    
    private void adjustAvailabilityDetail(String lostItemName, int lostItemQty, int effectiveDay){
        ItemNameConverter inc = new ItemNameConverter();
        int itemID = inc.getItemID(lostItemName);
        
        DBRecords counter = new DBRecords();
        int lastAvailabilityID = counter.getLastAvailabilityID();
        
        int availabilityID = effectiveDay;
        int newItemQty;
        while (availabilityID <= lastAvailabilityID){
            newItemQty = getNewItemQty(availabilityID, itemID, lostItemQty);
            setNewItemQty(availabilityID, itemID, newItemQty);
            availabilityID += 1;
        }
    }
    
    private int getNewItemQty(int availabilityID, int itemID, int lostItemQty){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT itemQty FROM ITEM WHERE availabilityID = ? AND itemID = ?");
            c.ps.setInt(1, availabilityID);
            c.ps.setInt(2, itemID);
            c.rs = c.ps.executeQuery();
            if (c.rs.next()){
               return c.rs.getInt("itemQty") - lostItemQty;
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(IncidentReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return ERROR_FLAG;
    }
    
    private void setNewItemQty(int availabilityID, int itemID, int newItemQty){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("UPDATE item SET itemQty = ? WHERE availabilityID = ? AND itemID = ?");
            c.ps.setString(1, String.valueOf(newItemQty));
            c.ps.setString(2, String.valueOf(availabilityID));
            c.ps.setString(3, String.valueOf(itemID));
            c.ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(IncidentReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private void setIncidentMessage(){
        incident.adjoin(String.format("    %1$-20s %2$-30s<br>", "Customer Name", custName));
        incident.adjoin(String.format("    %1$-20s %2$-30s<br>", "Effective Date", custReturnDate));
        if (incidentCommentary.isEmpty())
            incidentCommentary = "-";
        incident.adjoin(String.format("    %1$-20s %2$-30s<br>", "Commentary", incidentCommentary));
    }
    
    private void notification(){
        Style.initJOptionPane();
        JOptionPane.showMessageDialog(null, "Thank you for reporting!\nMrs. Andrion has been notified.", "Andrion", JOptionPane.INFORMATION_MESSAGE);
        
        Gmail mailer = new Gmail();
        String htmlMessage = 
            "<h1 style=\"color:teal;text-align:center\"><b>Equipment Incident Report</b></h1>" +
            "<br><h1 style=\"color:teal;\">Incident Details</h1>" +
            "<pre><font size =\"5\" face=\"Consolas\">" + incident.toString() + "</font></pre>" +
            "<br><h1 style=\"color:teal;\">Equipment Details</h1>" +
            "<pre><font size =\"5\" face=\"Consolas\">" + equipment.toString() + "</font></pre>";
            
        mailer.setSubject("MATS | Incident Report (Customer)");
        mailer.setMessage(htmlMessage);
        mailer.sendMail();
    }
    
    /* custStatus by deault is equal to 0, representing unfinished transaction.
     * custStatus is changed to 1, representing returned items.
     */
    private void changeCustStatus(NewConnection c) throws SQLException{
        c.ps = c.con.prepareStatement("UPDATE custDetail SET custStatus = ? WHERE custID = ?");
        c.ps.setInt(1, 1);
        c.ps.setInt(2, custID);
        c.ps.executeUpdate();
    }
}