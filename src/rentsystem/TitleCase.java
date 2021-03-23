
package rentsystem;

import java.util.Scanner;

/**
 *
 * @author Theresa De Ocampo
 */
public class TitleCase {
    public String getTitleCase(String text){
        String converted = "";
        String word;
        Scanner input = new Scanner(text);
        
        while(input.hasNext()) {
            word = input.next(); 
            converted += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " "; 
        }
        return converted.trim();
    }
}