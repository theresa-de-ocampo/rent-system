
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Theresa De Ocampo
 */
public class ForgottenPassword extends JFrame {
    private final int code;
    private final JPanel headerPanel = new JPanel();
    private final JPanel titlePanel = Style.createFormTitlePanel("Reset Password");
    private final JPanel contentPanel = new JPanel();
    private final JPanel errorPanel = new JPanel();
    private final JPanel errorPlaceHolderPanel = new JPanel();
    private final JPanel footerPanel = new JPanel();
    private String email;
    
    private JTextPane instructionsTextPane= new JTextPane();
    private final JLabel codeLabel = new JLabel("Verification Code");
    private final JTextField codeField = Style.createTextField(4);
    
    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            int codeEntry = Integer.parseInt(codeField.getText());
            if (codeEntry == code){
                dispose();
                ResetPassword rp = new ResetPassword();
                rp.setVisible(true);
            }
            else{
                errorPlaceHolderPanel.setVisible(false);
                errorPanel.setVisible(true);
            }
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public ForgottenPassword(int code, String email){
        super("Andrion");
        this.code = code;
        this.email = email;
        setSize(400, 400);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setHeaderPanelComponents();
        setContentPanelComponents();
        setFooterPanelComponents();
        addComponents();
    }
    
    private void setHeaderPanelComponents(){
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        try {
            int end = email.indexOf('@');
            email = maskString(email, 3, end, '*');
        } 
        catch (Exception ex) {
            Logger.getLogger(ForgottenPassword.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        instructionsTextPane = setPrompt("A message has been sent to your recovery email address, "  + email + ". Please open it and verify your account.");
        instructionsTextPane.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
    }
    
    private static String maskString(String strText, int start, int end, char maskChar) 
        throws Exception{
        
        if(strText == null || strText.equals(""))
            return "";
        
        if(start < 0)
            start = 0;
        
        if( end > strText.length() )
            end = strText.length();
            
        if(start > end)
            throw new Exception("End index cannot be greater than start index");
        
        int maskLength = end - start;
        
        if(maskLength == 0)
            return strText;
        
        StringBuilder sbMaskString = new StringBuilder(maskLength);
        
        for(int i = 0; i < maskLength; i++){
            sbMaskString.append(maskChar);
        }
        
        return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
    }
    
    private void setContentPanelComponents(){
        JLabel errorLabel = new JLabel("The code you entered is incorrect.");
        errorLabel.setFont(Style.FIELD_FONT);
        errorLabel.setForeground(Color.RED);
        errorPanel.add(errorLabel);
        errorPanel.setPreferredSize(new Dimension(400, 30));
        errorPanel.setVisible(false);
        errorPlaceHolderPanel.setPreferredSize(new Dimension(400, 30));
        
        codeLabel.setFont(Style.BODY_FONT);
        codeField.setFont(Style.FIELD_FONT);
        
        codeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        codeField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void setFooterPanelComponents(){
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 10));
        JButton submitButton = Style.createButton("Submit");
        submitButton.addActionListener(new SubmitListener());
        footerPanel.add(submitButton);
    }
    
    private void addComponents(){
        headerPanel.add(titlePanel);
        headerPanel.add(instructionsTextPane);
        
        contentPanel.add(errorPlaceHolderPanel);
        contentPanel.add(errorPanel);
        contentPanel.add(codeLabel);
        contentPanel.add(codeField);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    public static JTextPane setPrompt(String message){
        JTextPane prompt = new JTextPane();
        prompt.setText(message);
        StyledDocument doc = prompt.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
        int length = doc.getLength();
        try {
            doc.insertString(doc.getLength(), "",null);
        } 
        catch (BadLocationException ex) {
            Logger.getLogger(ForgottenPassword.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc.setParagraphAttributes(length+1, 1, center, false);
        prompt.setFont(Style.BODY_FONT);
        Color transparent = new Color(238, 238, 238);
        prompt.setBackground(transparent);
        prompt.setEditable(false);
        return prompt;
    }
}