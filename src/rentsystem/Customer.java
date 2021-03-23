
package rentsystem;

import java.util.Date;

/**
 *
 * @author Theresa De Ocampo
 */
public class Customer {
    private String fName;
    private String lName;
    private String contactNo;
    private Date rentDate;
    private Date returnDate;
    private String houseNo;
    private String street;
    private String barangay;
    private String cityOrTown;
    private String province;
    private String landmark;
    private final TitleCase tc = new TitleCase();
    
    public void setFName(String fName){
        this.fName = tc.getTitleCase(fName.trim());
    }
    
    public void setLName(String lName){
        this.lName = tc.getTitleCase(lName.trim());
    }
    
    public void setContactNo(String contactNo){
        this.contactNo = contactNo.trim();
    }
    
    public void setRentDate(Date rentDate){
        this.rentDate = rentDate;
    }
    
    public void setReturnDate(Date returnDate){
        this.returnDate = returnDate;
    }
    
    public void setHouseNo(String houseNo){
        this.houseNo = houseNo.trim();
    }
    
    public void setStreet(String street){
        street = street.trim();
        if (street.isEmpty())
            this.street = street;
        else
            this.street = street.substring(0, 1).toUpperCase() + street.substring(1);
    }
    
    public void setBarangay(String barangay){
        this.barangay = tc.getTitleCase(barangay.trim());
    }
    
    public void setCityOrTown(String cityOrTown){
        this.cityOrTown = tc.getTitleCase(cityOrTown.trim());
    }
    
    public void setProvince(String province){
        this.province = tc.getTitleCase(province.trim());
    }
    
    public void setLandmark(String landmark){
        landmark = landmark.trim();
        if (landmark.isEmpty())
            this.landmark = landmark;
        else
            this.landmark = landmark.substring(0, 1).toUpperCase() + landmark.substring(1);
    }
    
    public String getFName(){
        return fName;
    }
    
    public String getLName(){
        return lName;
    }
    
    public String getContactNo(){
        return contactNo;
    }
    
    public Date getRentDate(){
        return rentDate;
    }
    
    public Date getReturnDate(){
        return returnDate;
    }
    
    public String getHouseNo(){
        return houseNo;
    }
    
    public String getStreet(){
        return street;
    }
    
    public String getBarangay(){
        return barangay;
    }
    
    public String getCityOrTown(){
        return cityOrTown;
    }
    
    public String getProvince(){
        return province;
    }
    
    public String getLandmark(){
        return landmark;
    }
    
    public boolean requiredFieldsAreNotFilled(){
        return fName.isEmpty() || lName.isEmpty() || contactNo.isEmpty() || houseNo.isEmpty() || street.isEmpty() || barangay.isEmpty() || cityOrTown.isEmpty();
    }
    
    public boolean contactNoIsInvalid(){
        return !(contactNo.matches("^[0-9]+") && contactNo.length() == 11 && contactNo.charAt(0)== '0' && contactNo.charAt(1) == '9');
    }
    
    public boolean datesAreInvalid(){
        return rentDate.compareTo(returnDate) > 0;
    }
}