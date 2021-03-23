package rentsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.SQLException;

/**
 * 
 * @author Theresa De Ocampo
 * @version 13.0.1
 */
public class Login extends JFrame {
    private final JPanel headerPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();
    private final JPanel footerPanel = new JPanel();
    private final JPanel fieldPanel = new JPanel();
    private final JPanel errorPanel = new JPanel();
    private final JPanel errorPlaceHolderPanel = new JPanel();
    private JLabel imageLabel;
    private BufferedImage loginIcon;
    private final JLabel userLabel = new JLabel("Username");
    private final JTextField usernameField = Style.createTextField(13);
    private final JLabel passwordLabel = new JLabel("Password");
    private final JPasswordField passwordField = Style.createPasswordField(13);    
    private final JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
    private final JButton forgotPasswordButton = Style.createHyperlinkButton("Forgot Password?", 4, 20, 4, 4);
    private JButton loginButton = new JButton("Log In");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Login root = new Login();
        root.setVisible(true);
    }
    
    private class ShowPasswordListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (showPasswordCheckBox.isSelected())
                passwordField.setEchoChar((char) 0);
            else
                passwordField.setEchoChar('\u25CF');
        }
    }
    
    private class ForgotPasswordListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            Gmail mailer = new Gmail();
            int code = mailer.getCode();
            mailer.setSubject("MATS | Account Recovery");
            mailer.setMessage("<h1>Your verification code is " + code + ".</h1>");
            mailer.sendMail();
            String email = mailer.getEmail();
            
            // This implicitly calls the event listeners because
            // the default close operation was set to EXIT_ON_CLOSE
            dispose();
            
            ForgottenPassword fp = new ForgottenPassword(code, email);
            fp.setVisible(true);
        }
    }
    
    private class LogInListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            NewConnection c = new NewConnection();
            try {
                String inputUsername = usernameField.getText();
                String inputPassword = passwordField.getText();
                c.openConnection();
                c.ps = c.con.prepareStatement("SELECT * FROM user WHERE userID = ?");
                c.ps.setString(1, "1");
                c.rs = c.ps.executeQuery();
                if (c.rs.next()){
                    String dbPassword = c.rs.getString("userPassword");
                    String dbUsername = c.rs.getString("userName");
                    if (inputUsername.equals(dbUsername) && dbPassword.equals(inputPassword)){
                        dispose();
                        RentSystem driver = new RentSystem();
                        driver.setVisible(true);
                    }
                    else{
                        errorPlaceHolderPanel.setVisible(false);
                        errorPanel.setVisible(true);
                    }
                }
            } 
            catch (SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                c.closeConnection();
            }
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public Login(){
        super("Andrion");
        setSize(400, 530);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setHeaderPanelComponents();
        setContentPanelComponents();
        setFooterPanelComponents();
        addComponents();
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void setHeaderPanelComponents(){
        try {
            loginIcon = ImageIO.read(new File("External Files//Pictures//login.png"));
            imageLabel = new JLabel(new ImageIcon(loginIcon.getScaledInstance(125, 125, Image.SCALE_SMOOTH)));
        } 
        catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setContentPanelComponents(){
        JPanel userPanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        userLabel.setFont(Style.BODY_FONT);
        userLabel.setPreferredSize(new Dimension(100, 25));
        passwordLabel.setFont(Style.BODY_FONT);
        passwordLabel.setPreferredSize(new Dimension(100, 25));
        
        userPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        
        showPasswordCheckBox.setFont(Style.FIELD_FONT);
        showPasswordCheckBox.addActionListener(new ShowPasswordListener());
        forgotPasswordButton.addActionListener(new ForgotPasswordListener());
        
        JLabel errorLabel = new JLabel("Incorret username/password.");
        errorLabel.setFont(Style.FIELD_FONT);
        errorLabel.setForeground(Color.RED);
        errorPanel.setPreferredSize(new Dimension(300, 30));
        errorPanel.add(errorLabel);
        errorPanel.setVisible(false);
        
        errorPlaceHolderPanel.setPreferredSize(new Dimension(300, 30));
        
        userPanel.add(userLabel);
        userPanel.add(usernameField);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        fieldPanel.add(errorPlaceHolderPanel);
        fieldPanel.add(errorPanel);
        fieldPanel.add(userPanel);
        fieldPanel.add(passwordPanel);
        fieldPanel.add(showPasswordCheckBox);
        fieldPanel.add(forgotPasswordButton);
    }
    
    private void setFooterPanelComponents(){
        loginButton.setFont(Style.BODY_FONT);
        loginButton.setBackground(Style.BLUEGREEN);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton = Style.setButtonBorder(loginButton, 20);
        loginButton.addActionListener(new LogInListener());
        
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,30,10));
        footerPanel.add(loginButton);
    }
    
    private void addComponents(){
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30,10,10,10));
        headerPanel.add(imageLabel);
        
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(fieldPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
}