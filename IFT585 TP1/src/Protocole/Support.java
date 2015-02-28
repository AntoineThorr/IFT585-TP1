package Protocole;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe simulant le rôle du support de transmission.
 * @author Antoine Thorr
 */
public class Support {
    //Variables pour l'envoi du message
    Frame frameSentSource;
    Frame frameReceivedDest;
    boolean readySource = true;
    boolean receivedDest = false;
    
    //Variables pour l'envoi du ACK/NAK
    Frame frameSentDest;
    Frame frameReceivedSource;
    boolean readyDest = false;
    boolean receivedSource = false;
    
    int bufferSize;
    int error;

    Support(int bufferSize, int error) {
        this.bufferSize = bufferSize;
        this.error = error;
    }
    
    private void sendFrameSource(Frame f) {
        this.frameSentSource = f;
        this.readySource = false;
       
        try {
            Thread.sleep(this.bufferSize);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        this.frameReceivedDest = this.frameSentSource;
        this.readySource = true;
        this.receivedDest = true;
    }
    
    private void sendFrameDest(Frame f) {
        this.frameSentDest = f;
        this.readyDest = false;
       
        try {
            Thread.sleep(this.bufferSize);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        this.frameReceivedSource = this.frameSentDest;
        this.readyDest = true;
        this.receivedSource = true;
    }
    
    private Frame getFrameDest() {
        this.receivedDest = false;
        return this.frameReceivedDest;
    }
    
    private Frame getFrameSource() {
        this.receivedSource = false;
        return this.frameReceivedSource;
    }
    
    private boolean getReadySource() {
        return this.readySource;
    }
    
    private boolean getReadyDest() {
        return this.readyDest;
    }
    
    private boolean getReceivedDest() {
        return this.receivedDest;
    }
    
    private boolean getReceivedSource() {
        return this.receivedSource;
    }
}
