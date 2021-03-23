
package rentsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Theresa De Ocampo
 */
public class NewItemPhoto {
    private static String sourceDir;
    private final String category;
    private String fileName;
    private String inventoryImage;
    private boolean changePhotoDisplay = false;
    
    public NewItemPhoto(String category){
        this.category = category;
    }
    
    public void showFileExplorer(){
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView()); 
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.addChoosableFileFilter(imageFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int flag = fileChooser.showSaveDialog(null); 
        if (flag == 0){
            sourceDir = fileChooser.getSelectedFile().toString();
            fileName = fileChooser.getSelectedFile().getName();
            changePhotoDisplay = true;
        }
    }
    
    public void addPhotoToProjectFolder(){
        inventoryImage = "External Files//Pictures//MAItems//" + category + "//" + fileName;
        File sourceFile = new File(sourceDir);
        File destFile = new File(inventoryImage);
        try {
            copyFile(sourceFile, destFile);
        } 
        catch (IOException ex) {
            Logger.getLogger(NewItemPhoto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void copyFile(File sourceFile, File destFile)throws IOException {
        if (!sourceFile.exists()) 
            return;
        
        if (!destFile.exists()) 
            destFile.createNewFile();
        
        FileChannel source;
        FileChannel destination;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) 
            destination.transferFrom(source, 0, source.size());
        
        if (source != null) 
            source.close();
        
        if (destination != null) 
            destination.close();
    }
    
    public String getSource(){
        return sourceDir;
    }
    
    public String getInventoryImage(){
        return inventoryImage;
    }
    
    public boolean photoWasChanged(){
        return changePhotoDisplay;
    }
}