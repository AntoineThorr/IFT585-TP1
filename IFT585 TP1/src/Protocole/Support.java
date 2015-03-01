package Protocole;

/**
 * Classe simulant le rôle du support de transmission.
 * @author Antoine Thorr
 */
public class Support implements Runnable{
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
    
    @Override
    public void run() {
        
    }
    
    private void sendFrameSource(Frame f) {
        this.frameSentSource = f;
        this.readySource = false;
       
//        try {
//            Thread.sleep(this.bufferSize);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
        
        this.frameReceivedDest = this.frameSentSource;
        this.readySource = true;
        this.receivedDest = true;
    }
    
    public void sendFrameDest(Frame f) {
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
    
    public Frame getFrameDest() {
        this.receivedDest = false;
        return this.frameReceivedDest;
    }
    
    public Frame getFrameSource() {
        this.receivedSource = false;
        return this.frameReceivedSource;
    }
    
    public boolean getReadySource() {
        return this.readySource;
    }
    
    public boolean getReadyDest() {
        return this.readyDest;
    }
    
    public boolean getReceivedDest() {
        return this.receivedDest;
    }
    
    public boolean getReceivedSource() {
        return this.receivedSource;
    }
}
