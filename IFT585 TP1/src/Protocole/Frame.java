package Protocole;

/**
 *
 * @author Quentin
 */
public class Frame {   

    public int size;
    public String type;
    public int num;
    public byte[] data;
    
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
}