/**
 * 
 */
package by.bsuir.zuyeu.model.image;

import java.io.Serializable;

/**
 * @author Fieryphoenix
 * 
 */
public class ImagePacket implements Serializable {
    private static final long serialVersionUID = 6683702046379456099L;

    public static final int PACKET_SIZE = 30000;
    public static final int OBJECT_SIZE = 30120;

    private final byte[] data;
    private final long partIndex;

    protected ImagePacket() {
	data = new byte[PACKET_SIZE];
	partIndex = 0;
    }

    public ImagePacket(final byte[] data, final long index) {
	this.data = data;
	partIndex = index;
    }

    public byte[] getData() {
	return data;
    }

    public long getPartIndex() {
	return partIndex;
    }

}
