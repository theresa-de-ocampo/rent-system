
package rentsystem;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Theresa De Ocampo
 */
public class PickUpTable {
    private JTable table;
    private final JScrollPane tableScrollPane;
     
    public PickUpTable(String[][] tableContent, String label, int n, int heightSection){
        String[] columnTitles = {"Date", "ID", "Name", "Address", "Items"};
        table = new JTable(tableContent, columnTitles){
            @Override
            public String getToolTipText(MouseEvent e) {
                String tooltip = "";
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int columnIndex = columnAtPoint(p);
                String custID = getValueAt(rowIndex, 1).toString();
                String custContactNo = "";
                String custLandmark = "";
                
                NewConnection c = new NewConnection();
                try {
                    c.openConnection();
                    c.ps = c.con.prepareStatement("SELECT custContactNo, custLandmark FROM custDetail WHERE custID = " + custID);
                    c.rs = c.ps.executeQuery();
                    if (c.rs.next()){
                        custContactNo = c.rs.getString("custContactNo");
                        custLandmark = c.rs.getString("custLandmark");
                    }
                } 
                catch (SQLException ex) {
                    Logger.getLogger(PickUpTable.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    c.closeConnection();
                }
                
                switch (columnIndex) {
                    case 2:
                        tooltip = custContactNo;
                        break;
                    case 3:
                        if (custLandmark.isEmpty())
                            tooltip = "N/A";
                        else
                            tooltip = custLandmark;
                        break;
                    default:
                        tooltip = table.getValueAt(rowIndex, columnIndex).toString();
                        break;
                }
                return tooltip;
            }
        };
        
        table.setRowHeight(50);
        
        for (int r = 0; r < n; ++r){
            int heightRow = Integer.parseInt(tableContent[r][5]);
            if (heightRow > 50)
                table.setRowHeight(r, heightRow);
        }
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(130);
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(2).setPreferredWidth(250);
        columnModel.getColumn(3).setPreferredWidth(330);
        columnModel.getColumn(4).setPreferredWidth(270);
        
        table.setFont(Style.BODY_FONT);
        table.getTableHeader().setFont(Style.BODY_FONT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBackground(Style.AQUA);
        table.getTableHeader().setBackground(Style.BLUEGREEN);
        table.setIntercellSpacing(new Dimension(20,1));
        table.setPreferredScrollableViewportSize(new Dimension(1040, heightSection));
        table.setDefaultEditor(Object.class, null);
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int custID = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 1).toString());
                String custName = table.getValueAt(table.getSelectedRow(), 2).toString();
                ReturnForm driver = new ReturnForm(custID, custName);
                driver.setVisible(true);
            }
        });
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TitledBorder border = createBorder(label);
        tableScrollPane.setBorder(border);
    }
    
    public JScrollPane getTableSection(){
        return tableScrollPane;
    }
    
    public TitledBorder createBorder(String label){
        TitledBorder b = BorderFactory.createTitledBorder(label);
        b.setBorder(Style.THIN_LINE);
        b.setBorder(Style.THICK_LINE);
        Border border = b.getBorder();
        Border margin = new EmptyBorder(0, 10, 10, 10);
        b.setBorder(new CompoundBorder(border, margin));
        b.setTitleFont(Style.HEADER_FONT);
        return b;
    }
}