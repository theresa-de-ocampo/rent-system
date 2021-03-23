
package rentsystem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**Essential for PICK-UP DATES
 *
 * @author Theresa De Ocampo
 */
public class DateConverter {
    private final int thisWeek;
    
    
    // Essential for PICK-UP DATES
    public DateConverter(){
        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();   
        calendar.setTime(trialTime);     
        thisWeek = calendar.get(Calendar.WEEK_OF_YEAR); 
    }
    
    public int getNthDay(String dateDB){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy").toFormatter(Locale.UK);
        LocalDate bigEndian = LocalDate.parse(dateDB);
        return (bigEndian.getDayOfYear());
    }
    
    public int getNthDay(){
        return LocalDate.now().getDayOfYear();
    }
   
    // Marker Week: Sunday
    public boolean isThisWeek(String dbDate){
        return convertDateToWeekN(dbDate) == thisWeek;
    }
    
    public boolean isNextWeek(String dbDate){
        return convertDateToWeekN(dbDate) == thisWeek + 1;
    }
    
    public boolean isUpcoming(String dbDate){
        return convertDateToWeekN(dbDate) >= thisWeek + 2;
    }
    
    private int convertDateToWeekN(String dbDate){
        Calendar c = Calendar.getInstance();
        LocalDate ld = LocalDate.parse(dbDate);
        c.setTime(java.sql.Date.valueOf(ld));
        return c.get(Calendar.WEEK_OF_YEAR);
    }
}