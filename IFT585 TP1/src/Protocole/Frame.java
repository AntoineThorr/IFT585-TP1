package Protocole;

/**
 *
 * @author Quentin
 */
public class Frame {   

    private int size;
    private String type;
    private int num;
    private byte[] data;
    
    /**
     *
     * @param frameSize
     * @param type
     * @param frameID
     * @param frameData
     */
    public Frame(int frameSize, String type, int frameID, byte[] frameData){
        this.size = frameSize;
        this.type = type;
        this.num = frameID;
        this.data = frameData;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the num
     */
    public int getNum() {
        return num;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
    
    
}