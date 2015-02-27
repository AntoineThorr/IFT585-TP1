/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

/**
 *
 * @author Quentin
 */
public class Frame {
    public enum frameTypes{data, ack, nak};
    
    public int size;
    public frameTypes type;
    public int num;
    public byte[] data;
    
    public Frame(int frameSize, frameTypes frameType, int frameNum, byte[] frameData){
        this.size = frameSize;
        this.type = frameType;
        this.num = frameNum;
        this.data = frameData;
    }
}
