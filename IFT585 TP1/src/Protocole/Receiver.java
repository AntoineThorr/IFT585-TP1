package Protocole;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public class Receiver extends Station implements Runnable {

    String outputDir;
    int frameSize;
    int code;
    int reject;
    int rTimeOut;
    Support support;
    ArrayList<Byte> fileBuffer;

    Receiver(String outputDir, int frameSize, int code, int reject, int rTimeOut, Support s) {
        this.outputDir = outputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.reject = reject;
        this.rTimeOut = rTimeOut;
        this.support = s;
        this.fileBuffer = new ArrayList();
    }

    @Override
    public void run() {
//        Object lock = new Object();
//        synchronized (lock) { //A vérifier
//            if (!support.getReceivedDest()) {
//                try {
//                    this.wait();
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
        Frame f = support.getFrameDest();
        for (int i = 0; i < f.getData().length; i++) {
            this.fileBuffer.add(f.getData()[i]);
        }
        this.writeToFile();
        
        
    }
    
    private void writeToFile() {
        
        while(this.fileBuffer.iterator().hasNext()) {
            
        }
    }
}
