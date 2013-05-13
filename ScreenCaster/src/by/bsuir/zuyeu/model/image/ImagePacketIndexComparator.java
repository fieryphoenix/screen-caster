/**
 * 
 */
package by.bsuir.zuyeu.model.image;

import java.util.Comparator;

/**
 * @author Fieryphoenix
 * 
 */
public class ImagePacketIndexComparator implements Comparator<ImagePacket> {
    @Override
    public int compare(ImagePacket part1, ImagePacket part2) {
	if (part1.getPartIndex() == part2.getPartIndex()) {
	    return 0;
	} else if (part1.getPartIndex() > part2.getPartIndex()) {
	    return 1;
	} else {
	    return -1;
	}
    }

}
