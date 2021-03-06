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

    private static final long serialVersionUID = -3407489375939505898L;

    public static final int PACKET_SIZE = 30000;
    public static final int OBJECT_SIZE = 30120;

    private byte[] data;
    private long partIndex;

    private int parentWidth;
    private int parentHeight;
    private int startWidth;
    private int startHeight;
    private int chunkWidth;
    private int chunkHeight;

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

    public int getParentWidth() {
	return parentWidth;
    }

    public void setParentWidth(int parentWidth) {
	this.parentWidth = parentWidth;
    }

    public int getParentHeight() {
	return parentHeight;
    }

    public void setParentHeight(int parentHeight) {
	this.parentHeight = parentHeight;
    }

    public int getStartWidth() {
	return startWidth;
    }

    public void setStartWidth(int startWidth) {
	this.startWidth = startWidth;
    }

    public int getStartHeight() {
	return startHeight;
    }

    public void setStartHeight(int startHeight) {
	this.startHeight = startHeight;
    }

    public int getChunkWidth() {
	return chunkWidth;
    }

    public void setChunkWidth(int chunkWidth) {
	this.chunkWidth = chunkWidth;
    }

    public int getChunkHeight() {
	return chunkHeight;
    }

    public void setChunkHeight(int chunkHeight) {
	this.chunkHeight = chunkHeight;
    }

    public void setData(byte[] data) {
	this.data = data;
    }

    public void setPartIndex(long partIndex) {
	this.partIndex = partIndex;
    }

}
