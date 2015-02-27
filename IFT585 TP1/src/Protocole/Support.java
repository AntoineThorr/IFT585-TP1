package Protocole;

/**
 * Classe simulant le rôle du support de transmission.
 * @author Antoine Thorr
 */
public class Support {
    int bufferSize;
    int error;
    Station receiver;

    Support(int bufferSize, int error) {
        this.bufferSize = bufferSize;
        this.error = error;
        this.receiver = null;
    }
    
}
