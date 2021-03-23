
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Theresa De Ocampo
 */
public class InstructionWindow extends JFrame {
    JCheckBox instructionCheckBox = new JCheckBox("Don't show this message again.");
    
    class ImagePanel extends JComponent {
        private final Image image;
        
        public ImagePanel(Image image) {
            this.image = image;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
    }
    
    private class InstructionStatusListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if (instructionCheckBox.isSelected())
                changeInstructionStatus(1);
            else
                changeInstructionStatus(0);
        }
    }
    
    private class OkayListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public InstructionWindow(){
        setSize(1100, 618);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        Style.centerFrame(this);
        
        BufferedImage myImage = null;
        try {
            myImage = ImageIO.read(new File("External Files//Pictures//instruction.png"));
        } catch (IOException ex) {
            Logger.getLogger(InstructionWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        setContentPane(new ImagePanel(myImage));
        
        instructionCheckBox.setFont(Style.FIELD_FONT);
        instructionCheckBox.setBounds(75, 270, 280, 50);
        instructionCheckBox.setOpaque(false);
        instructionCheckBox.addActionListener(new InstructionStatusListener());
        
        JButton okayButton = Style.createButton("Okay");
        okayButton.setBackground(Style.BARLEY);
        okayButton.setBounds(130, 350, 140, 50);
        okayButton.addActionListener(new OkayListener());
        
        add(instructionCheckBox);
        add(okayButton);
    }

    private void changeInstructionStatus(int flag){
        NewConnection c = new NewConnection();
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("UPDATE user SET userInstructionStatus = ? WHERE userID = ?");
            c.ps.setInt(1, flag);
            c.ps.setInt(2, 1);
            c.ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(InstructionWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
    }
}