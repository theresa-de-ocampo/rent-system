
package rentsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
/**
 *
 * @author Theresa De Ocampo
 */
public class PickUpDate extends JPanel{
    public static final int COLUMNS = 5;
    private final String[][] records = new String[50][COLUMNS + 1];
    private String[][] tableContent;
    private String[][] thisWeekTableContent;
    private String[] thisWeekIDs;
    private final Storage valuesHolder = new Storage();
    private int heightSection = 0;
    private final JPanel tablesPanel = new JPanel();
    private final int recordsNextWeek;
    private final boolean recordExists;
    
    public PickUpDate(){
        setLayout(new BorderLayout());
        
        // Without holderPanel, the layout manager set to BoxLayout will
        // resize its components
        JPanel holderPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        
        populateJTable(1);
        int recordsThisWeek = valuesHolder.getN();
        int heightThisWeek = valuesHolder.getHeightSection();
        HeightGenerator h = new HeightGenerator();
        recordExists = displayTable(recordsThisWeek, heightThisWeek, h, "This Week");
        if (recordExists)
            copyTable();
        
        heightSection = 0;
        populateJTable(2);
        recordsNextWeek = valuesHolder.getN();
        int heightNextWeek = valuesHolder.getHeightSection();
        displayTable(recordsNextWeek, heightNextWeek, h, "Next Week");
        
        heightSection = 0;
        populateJTable(3);
        int recordsUpcoming= valuesHolder.getN();
        int heightUpcoming = valuesHolder.getHeightSection();
        displayTable(recordsUpcoming, heightUpcoming, h, "Upcoming");
        
        holderPanel.add(tablesPanel);
        if (recordsThisWeek == 0 && recordsNextWeek == 0 && recordsUpcoming == 0){
            JLabel noRecordsLabel = new JLabel("No Rentals Yet", JLabel.CENTER);
            noRecordsLabel.setFont(Style.SUBHEADER_FONT);
            noRecordsLabel.setForeground(Color.RED);
            add(noRecordsLabel, BorderLayout.CENTER);
        }
        else
            add(holderPanel, BorderLayout.CENTER);
    }
    
    public void populateJTable(int whenCode){
        fetchData(whenCode);
        int c;
        tableContent = new String[valuesHolder.getN()][COLUMNS + 1];
        thisWeekIDs = new String[valuesHolder.getN()];
        
        for (int r = 0; r < valuesHolder.getN(); ++r){
            for (c = 0; c < COLUMNS + 1; ++c){
                tableContent[r][c] = records[r][c];
            }
            thisWeekIDs[r] = records[r][1];
        }
    }
    
    // Searches for records according to their return date
    private void fetchData(int whenCode){
        NewConnection c = new NewConnection();
        int custID, custStatus, r = 0;
        String custName, custAddress, custReturnDate;
        DateConverter dc = new DateConverter();
        
        try {
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT * FROM custDetail");
            c.rs = c.ps.executeQuery();
            while (c.rs.next()){
                custStatus = c.rs.getInt("custStatus");
                if (custStatus == 0){
                    custID = Integer.parseInt(c.rs.getString("custID"));
                    custName = c.rs.getString("custFName");
                    custName += " " + c.rs.getString("custLName");
                    custAddress = "<html>" + c.rs.getString("custHouseNo") + " " + c.rs.getString("custStreet") + "<br>";
                    custAddress += c.rs.getString("custBarangay") + ", " + c.rs.getString("custCityOrTown");
                    if (c.rs.getString("custProvince").isEmpty())
                        custAddress += "</html>";
                    else
                        custAddress += ", " + c.rs.getString("custProvince");
                    custReturnDate = c.rs.getString("custReturnDate");

                    switch (whenCode){
                        case 1:
                            if (dc.isThisWeek(custReturnDate))
                                r = getRecords(r, custID, custReturnDate, custName, custAddress);
                            break;
                        case 2:
                            if (dc.isNextWeek(custReturnDate))
                                r = getRecords(r, custID, custReturnDate, custName, custAddress);
                            break;

                        case 3:
                            if (dc.isUpcoming(custReturnDate))
                                r = getRecords(r, custID, custReturnDate, custName, custAddress);
                            break;
                        default:
                            System.out.println("*** An unexpected error occurred at PickUpDate -> fetchDate ***");
                    }
                }
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(PickUpDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        valuesHolder.setN(r);
    }
    
    // Sets the array for JTable and retrieves the rentDetail of customer
    private int getRecords(int r, int custID, String custReturnDate, String custName, String custAddress){
        NewConnection c = new NewConnection();
        try {
            DateConverter dc = new DateConverter();
            TitleCase formatter = new TitleCase();
            records[r][0] = custReturnDate;
            records[r][1] = String.valueOf(custID);
            records[r][2] = custName;
            records[r][3] = custAddress;
            
            c.openConnection();
            c.ps = c.con.prepareStatement("SELECT * FROM rentDetail WHERE custID = ?");
            c.ps.setString(1, String.valueOf(custID));
            c.rs = c.ps.executeQuery();
            String items = "<html>";
            int heightRow = 0;
            while (c.rs.next()){
                items += formatter.getTitleCase(c.rs.getString("rentName"));
                items += " x ";
                items += c.rs.getString("rentQty");
                items += "<br>"; 
                heightRow += 25;
                heightSection += 25;
            }
            items += "</html>";
            records[r][4] = items;
            records[r][5] = String.valueOf(getValidatedHeight(heightRow));
            valuesHolder.setHeightSection(heightSection);
        } 
        catch (SQLException ex) {
            Logger.getLogger(PickUpDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            c.closeConnection();
        }
        return r + 1;
    }
    
    private boolean displayTable(int n , int height, HeightGenerator h, String title){
        if (n != 0){
            h.setHeight(height);
            height = h.getHeight();
            
            PickUpTable table = new PickUpTable(tableContent, title, n, height);
            JScrollPane tableScrollPane = table.getTableSection();
            tablesPanel.add(tableScrollPane);
            JPanel paddingPanel = new JPanel();
            paddingPanel.setPreferredSize(new Dimension(1040, 30));
            tablesPanel.add(paddingPanel);
            return true;
        }
        return false;
    }
    
    private void copyTable(){
        thisWeekTableContent = new String[tableContent.length][tableContent[0].length];
        for (int r = 0; r < tableContent.length; ++r)
            for (int c = 0; c < tableContent[r].length; ++c)
                thisWeekTableContent[r][c] = tableContent[r][c];
    }
    
    public int getValidatedHeight(int height){
        if (height < 50)
            return 50;
        else
            return height;
    }
}