
package rentsystem;

import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.Date;

/**
 *
 * @author Theresa De Ocampo
 */
public class DatePicker extends JPanel{
    private final com.toedter.calendar.JDateChooser dateChooser;
    
    public DatePicker(){
        dateChooser = new com.toedter.calendar.JDateChooser();
        JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor();
        editor.setEditable(false);
        dateChooser.getJCalendar().setMinSelectableDate(new Date()); 
        dateChooser.setPreferredSize(new Dimension(200, 30));
        dateChooser.setFont(Style.FIELD_FONT);
        dateChooser.setDate(new Date());
        add(dateChooser);
    }
    
    public java.util.Date getDate(){
        return dateChooser.getDate();
    }
}