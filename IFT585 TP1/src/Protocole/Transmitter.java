package Protocole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 *
 * @author Quentin
 */
public class Transmitter extends Station implements Runnable {

    String inputDir;
    int frameSize;
    int code;
    int sTimeOut;
    int frameID = 0;

    /**
     *
     * @param inputDir
     * @param frameSize
     * @param code
     * @param sTimeOut
     */
    public Transmitter(String inputDir, int frameSize, int code, int sTimeOut) {
        this.inputDir = inputDir;
        this.frameSize = frameSize;
        this.code = code;
        this.sTimeOut = sTimeOut;
    }

    @Override
    public void run() {
        int bufferSize = (int) Math.pow(2, this.frameSize) - 1;
        CircularFifoQueue buffer = new CircularFifoQueue(bufferSize);

        byte[] data = readFile(this.inputDir);

        while (data.length - this.frameID * this.frameSize > this.frameSize) {
            buffer.add(genFrame(data));
            //TODO - Checker le booléen "peut envoyer" ?
            //TODO - Si peut envoyer, gérer l'envoi (transmettre au support)
        }
        
        

    }

    private byte[] readFile(String p) {
        Path path = Paths.get(p);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
            for (int i = 0; i < data.length; i++) {
                System.out.println(i + " -> " + data[i]);
            }
            return data;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /**
     * Générateur de la prochaine frame. La méthode Arrays.copyOfRange permet
     * d'éviter l'étape de remplissage lorsque la taille de fin (stop) excède la
     * taille du tableau, puisqu'il complète automatiquement avec le byte 0.
     *
     * @param data ensemble des bytes du message d'entrée
     * @return prochaine frame générée à partir du compteur
     */
    private Frame genFrame(byte[] data) {
        int start = this.frameID * this.frameSize;
        int stop = (this.frameID + 1) * this.frameSize;
        byte[] frameData = Arrays.copyOfRange(data, start, stop);
        Frame f = new Frame(this.frameSize, "data", this.frameID, frameData);

        this.frameID++;

        return f;
    }
}
