package Protocole;

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

    Receiver(String outputDir, int frameSize, int code, int reject, int rTimeOut) {
        this.outputDir = outputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.reject = reject;
        this.rTimeOut = rTimeOut;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
