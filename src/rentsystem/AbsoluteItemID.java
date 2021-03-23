
package rentsystem;

/**
 *
 * @author Theresa De Ocampo
 */
public class AbsoluteItemID {
    private int itemID;
    private ItemOffset io = new ItemOffset();
    
    public AbsoluteItemID(String category, int i){
        switch(category){
            case ItemOffset.BUFFET_LINE_CATEGORY:
                itemID = i + 1;
                break;
            case ItemOffset.FURNITURE_CATEGORY:
                itemID = i + io.getOffset(ItemOffset.FURNITURE_CATEGORY);
                break;
            case ItemOffset.FLATWARE_CATEGORY:
                itemID = i + io.getOffset(ItemOffset.FLATWARE_CATEGORY);
                break;
            case ItemOffset.CARRIER_CATEGORY:
                itemID = i + io.getOffset(ItemOffset.CARRIER_CATEGORY);
                break;
            default:
                System.out.println("An unexpected error occurred at AbsoluteItemID");
        }
    }
    
    public int getAbsoluteItemID(){
        return itemID;
    }
}