
package rentsystem;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;

/**
 *
 * @author Theresa De Ocampo
 */
public class Gmail {
    private int code;
    private String senderAddress;
    private String subject;
    private String htmlMessage;
    
    public Gmail(){
        Random codeGenerator = new Random(); 
        code = codeGenerator.nextInt(9999); 
    }
    
    public void sendMail(){
        NewConnection c = new NewConnection();
        String senderPassword = "";
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT userRecoveryEmail, userRecoveryPassword FROM user WHERE userID = 1");
            c.rs = c.ps.executeQuery();
            if (c.rs.next()){
                senderAddress = c.rs.getString("userRecoveryEmail");
                senderPassword = c.rs.getString("userRecoveryPassword");
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(Gmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        
        
        String recipientAddress = senderAddress;
        final String emailAddress = senderAddress;
        final String password = senderPassword;
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(emailAddress, password);
            }
        });
        
        Message message = prepareMessage(session, senderAddress, recipientAddress);
        try {  
            Transport.send(message);
        } 
        catch (MessagingException ex) {
            Logger.getLogger(Gmail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Message prepareMessage(Session session, String emailAddress, String recipient){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAddress));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            
            message.setSubject(subject);
            message.setContent(htmlMessage, "text/html");
            return message;
        } 
        catch (MessagingException ex) {
            Logger.getLogger(Gmail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int getCode(){
        return code;
    }
    
    public String getEmail(){
        return senderAddress;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    
    public void setMessage(String message){
        this.htmlMessage = message;
    }
}