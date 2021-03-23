
package rentsystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author Theresa De Ocampo
 */
public class Summary extends JFrame {
    JPanel contentPanel = new JPanel();
    JPanel itemPanel = new JPanel();
    int custID;
    PSTConverter pc;
    private final JPanel tablePanel = new JPanel();
    private JTable table;
    private int n = 0;
    private boolean proceed = false;
    
    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
            String tableMessage = getSummary(custID);
            String headerMessage = getCustDetail(custID) + "</html>";
            Style.initJOptionPane();
            JOptionPane.showMessageDialog(null, headerMessage + "\n" + tableMessage, "Andrion", JOptionPane.INFORMATION_MESSAGE);
            Main.contentPanel.removeAll();
            RentDetails.setCustID(0);
            Main.homeButton.doClick();
        }
    }
    
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public Summary(int custID, PSTConverter pc){
        setSize(440, 470);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        this.custID = custID;
        this.pc = pc;
        
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));
        JTextArea isntructionsTextArea = Style.createStaticTextArea(3, 20, "Double click the quantity column to change its value.");
        contentPanel.add(isntructionsTextArea);
        showTable();
        
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        GridBagLayout gl = new GridBagLayout();
        buttonsPanel.setLayout(gl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        
        JButton submitButton = Style.createButton("Submit");
        gl.setConstraints(submitButton, gbc);
        submitButton.addActionListener(new SubmitListener());
        
        JButton cancelButton = Style.createButton("Cancel");
        gl.setConstraints(cancelButton, gbc);
        cancelButton.addActionListener(new CancelListener());
        
        buttonsPanel.add(submitButton);
        buttonsPanel.add(cancelButton);
        
        
        add(Style.createFormTitlePanel("Summary"), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private void showTable(){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT COUNT(1) n FROM rentDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();
            
            if (c.rs.next())
                n = Integer.parseInt(c.rs.getString("n"));
            else
                System.out.println("An unexpected error occurred at IncidentReport -> getJTable");
            
            String[][] tableContent = new String[n][2];
            c.ps = c.con.prepareStatement("SELECT rentName, rentQty FROM rentDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();
            
            int i = 0;
            TitleCase formatter = new TitleCase();
            while (c.rs.next()){
                tableContent[i][0] = formatter.getTitleCase(c.rs.getString("rentName"));
                tableContent[i++][1] = c.rs.getString("rentQty");
            }
            
            String[] columnTitles = {"Item", "Qty"};
            table = new JTable(tableContent, columnTitles){
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 1;        // make all fields read-only except column 1
                }
            };
            JScrollPane tableScrollPane = Style.setFormTableProperties(table);
            tablePanel.add(tableScrollPane);
            contentPanel.add(tablePanel);
        } 
        catch (SQLException ex) {
            Logger.getLogger(ReturnForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private String getCustDetail(int custID){
        NewConnection c = new NewConnection();
        String message = "<html>Reservation for ";
        ChangeableString messageMaker = new ChangeableString(message);
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT custFName, custLName FROM custDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();
            if (c.rs.next())
                messageMaker.adjoin(c.rs.getString("custFName") + " " + c.rs.getString("custLName") + " from<br><b>" +  pc.getRentLongDate() + "</b> to <b>" + pc.getReturnLongDate() + "</b><br> is complete!");
        } 
        catch (SQLException ex) {
            Logger.getLogger(Summary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return messageMaker.toString();
    }
    
    public String getSummary(int custID){
        String message = "";
        ChangeableString messageMaker = new ChangeableString(message);
        TitleCase formatter = new TitleCase();
        NewConnection c = new NewConnection();
        try{
            c.openConnection();
            messageMaker.adjoin(String.format("\n    %1$-20s %2$10s\n", "ITEM", "QUANTITY"));
            c.ps = c.con.prepareStatement("SELECT rentName, rentQty FROM rentDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();

            while(c.rs.next()){
                String item_detail = String.format("    %1$-20s %2$8d\n", formatter.getTitleCase(c.rs.getString("rentName")), Integer.parseInt(c.rs.getString("rentQty")));
                messageMaker.adjoin(item_detail);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(Summary.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return messageMaker.toString();
    }
    
    public boolean shallProceed(){
        return proceed;
    }
}