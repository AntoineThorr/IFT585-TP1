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
    public enum frameType{data, ack, nack};
    
    public int frameSize;
    public frameType type;
    public int seq_num;
    public int  ack_num;
    public char[] data;
    
    public Frame( ){
        
    }
    
}
