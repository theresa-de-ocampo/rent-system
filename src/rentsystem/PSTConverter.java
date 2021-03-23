
package rentsystem;

/** Essential for HOME -> Rent Details -> Summary
 *
 * @author Theresa De Ocampo
 */
public class PSTConverter {
    private String rentLongDate;
    private String returnLongDate;
   
    public void setRentLongDate(String pst){
        rentLongDate = pstToLong(pst);
    }
    
    public void setReturnLongDate(String pst){
        returnLongDate = pstToLong(pst);
    }
    
    private String pstToLong(String pst){
        String longDate = "[" + pst.substring(0, 3) + "]" + pst.substring(3, 10) + ", " + pst.substring(24, 28);
        return longDate;
    }
    
    public String getRentLongDate(){
        return rentLongDate;
    }
    
    public String getReturnLongDate(){
        return returnLongDate;
    }
}