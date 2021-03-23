
package rentsystem;

/**
 *
 * @author Theresa De Ocampo
 */
public class ChangeableString {
    private String str;

    public ChangeableString(String str) {
        this.str = str;
    }

    public void adjoin(String continuation) {
        str += continuation;
    }

    @Override
    public String toString() {
        return str;
    }
}