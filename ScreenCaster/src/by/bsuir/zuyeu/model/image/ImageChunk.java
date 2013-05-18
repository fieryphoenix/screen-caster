/**
 * 
 */
package by.bsuir.zuyeu.model.image;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author Fieryphoenix
 * 
 */
public class ImageChunk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8227741756182224375L;

    private int parentWidth;
    private int parentHeight;
    private int startWidth;
    private int startHeight;
    private int chunkWidth;
    private int chunkHeight;

    private BufferedImage imageChunk;

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

    public BufferedImage getImageChunk() {
	return imageChunk;
    }

    public void setImageChunk(BufferedImage imageChunk) {
	this.imageChunk = imageChunk;
    }

}
