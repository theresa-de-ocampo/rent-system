
package rentsystem;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Kaye Ann Javier
 * @author Guian Angelo Salamat
 */
public class InventoryReport extends JFrame {
    private final String inventoryIncidentName;
    private final int inventoryIncidentQty;
    private final JPanel commentaryPanel = new JPanel();
    private final JTextArea commentaryField = new JTextArea(4, 18);
    
    private class SubmitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
            notification();
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
    public InventoryReport(String itemName, int itemQty){
        super("Andrion");
        setSize(440, 380);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        
        addWindowListener(new CloseListener());  
        
        this.inventoryIncidentName = itemName;
        this.inventoryIncidentQty = itemQty;
        
        JPanel instructionsPanel = new JPanel();
        JLabel instructionsLabel = Style.createLabel("Describe the incident.", 0);
        instructionsPanel.add(instructionsLabel);
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        showCommentaryPanel();
        
        JPanel buttonPanel = new JPanel();
        JButton submitButton = Style.createButton("Submit");
        submitButton.addActionListener(new SubmitListener());
        buttonPanel.add(submitButton, new GridBagConstraints());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(instructionsPanel);
        contentPanel.add(commentaryPanel);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(Style.createFormTitlePanel("Equipment Incident Report"), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
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
    
    private void notification(){
        Style.initJOptionPane();
        JOptionPane.showMessageDialog(null, "Thank you for reporting!\nMrs. Andrion has been notified.", "Andrion", JOptionPane.INFORMATION_MESSAGE);
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        String effectiveDate = dtf.format(localDate);
        
        ChangeableString detail = new ChangeableString("");
        detail.adjoin(String.format("    %1$-15s %2$-20s<br>", "Effective Date", effectiveDate));
        detail.adjoin(String.format("    %1$-15s %2$-20s<br>", "Commentary", inventoryIncidentQty + " piece(s) of " + inventoryIncidentName));
        detail.adjoin(String.format("    %1$-15s %2$-20s<br>", "", commentaryField.getText()));
        
        Gmail mailer = new Gmail();
        String htmlMessage = 
            "<h1 style=\"color:teal;text-align:center\"><b>Equipment Incident Report</b></h1>" + 
            "<br><pre><font size =\"5\" face=\"Consolas\">" + detail.toString() + "</font></pre>";
            
        mailer.setSubject("MATS | Incident Report (Inventory)");
        mailer.setMessage(htmlMessage);
        mailer.sendMail();
    }
}