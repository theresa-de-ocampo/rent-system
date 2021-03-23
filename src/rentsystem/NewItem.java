
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Theresa De Ocampo
 */
public class NewItem extends JFrame{
    private final String category;
    private final JPanel headerPanel;
    private final JPanel contentPanel = new JPanel();
    
    private final JTextField itemNameField = Style.createTextField(10);
    private final JTextField itemQtyField = Style.createTextField(5);
    private JLabel imageLabel = null;
    private final NewItemPhoto dirHandler;
    
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            TitleCase formatter = new TitleCase();
            dirHandler.addPhotoToProjectFolder();
            String name = formatter.getTitleCase(itemNameField.getText().trim());
            int qty = Integer.parseInt(itemQtyField.getText().trim());
            String photoDir = dirHandler.getInventoryImage();
            NewItemDB dbHandler = new NewItemDB(category, name, qty, photoDir);
            dispose();
            Style.initJOptionPane();
            JOptionPane.showMessageDialog(
                null, 
                name + " was successfully added!", 
                "Andrion",
                JOptionPane.INFORMATION_MESSAGE);
            Main.inventoryButton.doClick();
        }
    }
    
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dispose();
        }
    }
    
    private class AddPhotoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            dirHandler.showFileExplorer();
            if (dirHandler.photoWasChanged()){
                try {
                    BufferedImage newImage = ImageIO.read(new File(dirHandler.getSource()));
                    imageLabel.setIcon(new ImageIcon(newImage.getScaledInstance(160, 160, Image.SCALE_SMOOTH)));
                    imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                } 
                catch (IOException ex) {
                    Logger.getLogger(NewItem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    // This entire system is in a single-threading setting
    @SuppressWarnings("LeakingThisInConstructor")
    public NewItem(String category){
        super("Andrion");
        setSize(530, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Style.centerFrame(this);
        this.category = category;
        dirHandler = new NewItemPhoto(category);
        
        headerPanel = Style.createFormTitlePanel("Add New " + category + " Item");
        setContentPanelComponents();
        addComponents();
    }
    
    private void setContentPanelComponents(){
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JPanel itemNamePanel = new JPanel();
        JPanel itemQtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel buttonsPanel = new JPanel();
        
        itemNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        itemQtyPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        itemQtyPanel.setPreferredSize(new Dimension(300, 55));
        
        JLabel itemNameLabel = Style.createLabel("Item Name", 0);
        JLabel itemQtyLabel = Style.createLabel("Quantity", 0);
        itemQtyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        
        JButton saveButton = Style.createButton("Save");
        JButton cancelButton = Style.createButton("Cancel");
        saveButton.addActionListener(new SaveListener());
        cancelButton.addActionListener(new CancelListener());
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Style.BARLEY);
        rightPanel.setBorder(Style.THICK_LINE);
        try {
            BufferedImage uploadIcon = ImageIO.read(new File("External Files//Pictures//upload.png"));
            imageLabel = new JLabel(new ImageIcon(uploadIcon.getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        } 
        catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        JButton addPhotoButton = Style.createHyperlinkButton("+ Add Photo", 0, 35, 10, 0);
        addPhotoButton.addActionListener(new AddPhotoListener());
        
        itemNamePanel.add(itemNameLabel);
        itemNamePanel.add(itemNameField);
        itemQtyPanel.add(itemQtyLabel);
        itemQtyPanel.add(itemQtyField);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        
        leftPanel.add(itemNamePanel);
        leftPanel.add(itemQtyPanel);
        leftPanel.add(buttonsPanel);
        rightPanel.add(imageLabel);
        rightPanel.add(addPhotoButton);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
    }
    
    private void addComponents(){
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
}