
package rentsystem;

/**
 *
 * @author Theresa De Ocampo
 */
public class HeightGenerator {
    private int height;
    
    public void setHeight(int height){
        this.height = height;
    }
    
    public int getEstimate(int a, int b){
        int c1 = a - (a % b);
        int c2 = (a + b) - (a % b);
        if (a - c1 > c2 - a) 
            return c2;
        else 
            return c1;
    }
    
    /*
     * If height of all records are greater than 200 pixels, 
     *     divide it by two, and round it to the highest number
     *     divisible by 25 (since each line equals 25 pixels).
     * Set height to 200 if the supposed height is in between [100, 200)
     * Height will be as it is if it is less than or equal to 100
     * Set height to 50 pixels if there is only 1 record
     */
    public int getHeight(){
        if (height > 200){
            int temp = height / 2;
            if (temp > 200)
                height = getEstimate(temp, 25);
        }
        else if (height > 100)
            height = 200;
        else if (height == 25)
            height = 50;
        else
            if (height % 50 != 0)
                height += 25;
        return height;     
    }
}