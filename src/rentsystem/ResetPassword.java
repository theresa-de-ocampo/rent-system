
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author Theresa De Ocampo
 */
public class ResetPassword extends JFrame {
    private final JPanel headerPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();
    private final JPanel footerPanel = new JPanel();
    private final JPanel titlePanel = Style.createFormTitlePanel("Reset Password");
    private final JPanel errorPanel = new JPanel();
    private final JPanel errorPlaceHolderPanel = new JPanel();
    private final JLabel passwordLabel = new JLabel("New Password");
    private final JLabel confirmPasswordLabel = new JLabel("Confirm Password");
    private final JPasswordField passwordField = Style.createPasswordField(13);
    private final JPasswordField confirmPasswordField = Style.createPasswordField(13);
    private final JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
    
    private class ShowPasswordListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (showPasswordCheckBox.isSelected()){
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            }
            else{
                passwordField.setEchoChar('\u25CF');
                confirmPasswordField.setEchoChar('\u25CF');
            }
        }
    }
    
    private class SubmitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            String passwordEntry = passwordField.getText();
            String confirmPasswordEntry = confirmPasswordField.getText();
            if (passwordEntry.equals(confirmPasswordEntry)){
                dispose();
                NewConnection c = new NewConnection();
                try {
                    c.openConnection();
                    c.ps = c.con.prepareStatement("UPDATE user SET userPassword = ? WHERE userID = ?");
                    c.ps.setString(1, passwordEntry);
                    c.ps.setString(2, String.valueOf(1));
                    c.ps.executeUpdate();
                } 
                catch (SQLException ex) {
                    Logger.getLogger(ResetPassword.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    c.closeConnection();
                }
                RentSystem driver = new RentSystem();
                driver.setVisible(true);
            }
            else{
                errorPlaceHolderPanel.setVisible(false);
                errorPanel.setVisible(true);
            }
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public ResetPassword(){
        super("Andrion");
        setSize(400, 480);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        setContentPanelComponents();
        setFooterPanelComponents();
        addComponents();
    }
    
    private void setContentPanelComponents(){
        JLabel errorLabel = new JLabel("Passwords do not match.");
        errorLabel.setFont(Style.FIELD_FONT);
        errorLabel.setForeground(Color.RED);
        errorPanel.add(errorLabel);
        errorPanel.setPreferredSize(new Dimension(400, 40));
        errorPanel.setVisible(false);
        errorPlaceHolderPanel.setPreferredSize(new Dimension(400, 40));
        
        passwordLabel.setFont(Style.BODY_FONT);
        passwordLabel.setPreferredSize(new Dimension(200, 25));
        confirmPasswordLabel.setFont(Style.BODY_FONT);
        confirmPasswordLabel.setPreferredSize(new Dimension(200, 25));
        
        passwordField.setFont(Style.FIELD_FONT);
        passwordField.setBorder(Style.FIELD_MARGIN);
        passwordField.setEchoChar('\u25CF');
        confirmPasswordField.setFont(Style.FIELD_FONT);
        confirmPasswordField.setBorder(Style.FIELD_MARGIN);
        confirmPasswordField.setEchoChar('\u25CF');
        
        showPasswordCheckBox.setFont(Style.FIELD_FONT);
        showPasswordCheckBox.addActionListener(new ShowPasswordListener());
    }
    
    private void setFooterPanelComponents(){
        JButton submitButton = Style.createButton("Submit");
        submitButton.addActionListener(new SubmitListener());
        footerPanel.add(submitButton);
    }
    
    private void addComponents(){
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 20, 10));
        headerPanel.add(titlePanel);
        
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(errorPlaceHolderPanel);
        contentPanel.add(errorPanel);
        contentPanel.add(passwordLabel);
        contentPanel.add(passwordField);
        JPanel emptyPanel1 = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(200, 25));
        contentPanel.add(emptyPanel1);
        contentPanel.add(confirmPasswordLabel);
        contentPanel.add(confirmPasswordField);
        JPanel emptyPanel2 = new JPanel();
        emptyPanel2.setPreferredSize(new Dimension(200, 20));
        contentPanel.add(emptyPanel2);
        contentPanel.add(showPasswordCheckBox);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
}