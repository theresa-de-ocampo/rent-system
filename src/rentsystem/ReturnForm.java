
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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Theresa De Ocampo
 */
public class ReturnForm extends JFrame{
    private final int custID;
    private final String custName;
    private final JPanel contentPanel = new JPanel();
    private final JPanel tablePanel = new JPanel();
    private JTable table;
    private int n = 0;
    private int[] qtyBasis;
    private int numberOfLostItems = 0;
    private String[][] lostItemsTableContent;
    
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
        }
    }
    
    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            Style.initJOptionPane();
            int answer = JOptionPane.showConfirmDialog(
                null, 
                "Submit this form?", 
                "Andrion",
                JOptionPane.YES_NO_OPTION);
            if (answer == 0){
                if (validInput()){
                    if (incidentOccurred()){
                        dispose();
                        IncidentReport reporter = new IncidentReport(custID, numberOfLostItems, lostItemsTableContent);
                        reporter.setVisible(true);
                    }
                }
                
            }
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public ReturnForm(int custID, String custName){
        setSize(440, 470);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        
        this.custID = custID;
        this.custName = custName;
        
        JPanel headerPanel = Style.createFormTitlePanel("Equipment Return Form");
        setContentPanelComponents();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setContentPanelComponents(){
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        JPanel namePanel = new JPanel();
        JLabel instructionsLabel = new JLabel("Edit the quantity column accordingly.");
        instructionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructionsLabel.setFont(Style.BODY_FONT);
        
        namePanel.setBorder(Style.createSeparator(custName, Style.BODY_FONT));
        showTable();
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        GridBagLayout gl = new GridBagLayout();
        buttonsPanel.setLayout(gl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 30, 0, 30);
        
        JButton submitButton = Style.createButton("Submit");
        gl.setConstraints(submitButton, gbc);
        submitButton.addActionListener(new SubmitListener());
        
        JButton cancelButton = Style.createButton("Cancel");
        gl.setConstraints(cancelButton, gbc);
        cancelButton.addActionListener(new CancelListener());
        
        buttonsPanel.add(submitButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(namePanel);
        contentPanel.add(instructionsLabel);
        contentPanel.add(tablePanel);
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
            setQtyBasis(tableContent);
            
            String[] columnTitles = {"Item", "Qty"};
            table = new JTable(tableContent, columnTitles){
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 1;        // make all fields read-only except column 1
                }
            };
            JScrollPane tableScrollPane = Style.setFormTableProperties(table);
            tablePanel.add(tableScrollPane);
        } 
        catch (SQLException ex) {
            Logger.getLogger(ReturnForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
    
    private void setQtyBasis(String[][] tableContent){
        qtyBasis = new int[n];
        for (int i = 0; i < n; ++i)
            qtyBasis[i] = Integer.parseInt(tableContent[i][1]);
    }
    
    private boolean incidentOccurred(){
        lostItemsTableContent = new String[n][2];
        int qtyReturned;
        int qtyLost;
        for (int i = 0; i < n; ++i){
            if (qtyBasis[i] != Integer.parseInt(table.getModel().getValueAt(i, 1).toString())){
                qtyReturned = Integer.parseInt(table.getModel().getValueAt(i, 1).toString());
                qtyLost = qtyBasis[i] - qtyReturned;
                
                if (qtyLost != 0){
                    lostItemsTableContent[numberOfLostItems][0] = String.valueOf(table.getModel().getValueAt(i, 0));
                    lostItemsTableContent[numberOfLostItems++][1] = String.valueOf(qtyLost);
                }
            }
        }
        return numberOfLostItems > 0;
    }
    
    private boolean validInput(){
        for (int i = 0; i < n; ++i){
            if (qtyBasis[i] < Integer.parseInt(table.getModel().getValueAt(i, 1).toString())){
                Style.initJOptionPane();
                JOptionPane.showMessageDialog(null, "Invalid input!\nReturned quantity should not be greater than rented quantity.", "Andrion", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}