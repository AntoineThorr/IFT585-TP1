package Protocole;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objet permettant de représenter un support de transmission.
 *
 * TODO - Erreurs, latence
 */
public class Support extends Thread {

    // Trame que la source veut envoyer.
    private Frame sendSource;

    // Trame reçu par la destination.
    private Frame receivedAtDestination;

    // Le support est prêt à envoyer une trame de la source (TRUE)
    // La trame est en attente d'être envoyé à la destination (FALSE)
    private boolean readyToSendSource;

    // Le support a envoyé une trame à la destination qui ne l'a pas encore traité (TRUE)
    // Il n'y a pas de trame en attente du côté de la destination (FALSE)
    private boolean dataReceivedAtDest;

    // Variables pour l'envoi du ACK/NAK
    // Trame que la destination veut envoyer.
    private Frame sendDestination;

    // Trame reçue par la source.
    private Frame receivedAtSource;

    // Le support est prêt à envoyer une trame de la destination (TRUE)
    // La trame est en attente d'être envoyé à la source (FALSE)
    private boolean readyToSendDestination;

    // Le support a envoyé une trame à la source qui ne l'a pas encore traité (TRUE)
    // Il n'y a pas de trame en attente du côté de la source (FALSE)
    private boolean dataReceivedAtSource;

    // Par simplicité, le support est unidirectionnel.
    private int sourceNumber = 0;
    private int destinationNumber = 0;

//Délai de transmission sur le support
    private final int sDelay;

    // Type d'erreur
    private int typeError;
    final static private int NOERROR = 0;
    final static private int CORRUPTION = 1;
    final static private int LOSTFRAME = 2;

    private int errorCounter;
    final static private int errorOccurence = 10;

    /**
     * Constructeur de la classe Support
     *
     * @param sourceNumber      Numéro (ID) de la source
     * @param destinationNumber Numéro (ID) du récepteur
     * @param error             Numéro (ID) de l'erreur à générer
     */
    public Support(int sourceNumber, int destinationNumber, int delay, int error) {
        this.sourceNumber = sourceNumber;
        this.destinationNumber = destinationNumber;
        readyToSendSource = readyToSendDestination = true;
        dataReceivedAtDest = dataReceivedAtSource = false;
        this.sDelay = delay;
        this.typeError = error;
        this.errorCounter = 0;
    }

    // 
    /**
     * Fonction appelée par une station afin de savoir si elle peut envoyer une
     * trame sur le support.
     *
     * @param stationNumber Numéro (ID) de la station d'émission
     * @return TRUE si la station peut émettre, FALSE sinon
     */
    public synchronized boolean isReadyToSend(int stationNumber) {
        if (stationNumber == sourceNumber) {
            return readyToSendSource;
        } else if (stationNumber == destinationNumber) {
            return readyToSendDestination;
        }
        return false;
    }

    /**
     * Fonction appelée par une station afin de savoir si elle a reçu une trame.
     *
     * @param stationNumber Numéro de la station de réception
     * @return TRUE si la station peut récupérer, FALSE sinon
     */
    public synchronized boolean asReceivedData(int stationNumber) {
        if (stationNumber == destinationNumber) {
            return dataReceivedAtDest;
        } else if (stationNumber == sourceNumber) {
            return dataReceivedAtSource;
        }
        return false;
    }

    /**
     * Fonction appelée par une station pour envoyer une trame sur le support.
     *
     * TODO - Gestion des exceptions.
     *
     * @param frame         Frame à envoyer sur le support
     * @param stationNumber Numéro (ID) de la station de destination
     */
    public synchronized void sendFrame(Frame frame, int stationNumber) {
        if (stationNumber == sourceNumber) {
            readyToSendSource = false;
            sendSource = frame;
        } else if (stationNumber == destinationNumber) {
            readyToSendDestination = false;
            sendDestination = frame;
        }
    }

    /**
     * Fonction appelée par une station afin de récupérer une trame reçue.
     *
     * TODO - Gestion des exceptions. La fonction peut être appelée même si le
     * support est "occupé".
     *
     * @param stationNumber
     * @return
     */
    public synchronized Frame retrieveData(int stationNumber) {
        if (stationNumber == destinationNumber) {
            dataReceivedAtDest = false;
            return receivedAtDestination;
        } else if (stationNumber == sourceNumber) {
            dataReceivedAtSource = false;
            return receivedAtSource;
        }
        return null;
    }

    /**
     * Fonction permettant de redéfinir la source. N'est plus vraiment
     * nécessaire.
     *
     * TODO - Vérifier l'utilité, la supprimer si non nécessaire
     *
     * @param sourceNumber Numéro (ID) de la station source
     */
    public void setSource(int sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    /**
     * Fonction permettant de redéfinir la destination. N'est plus vraiment
     * nécessaire.
     *
     * TODO - Vérifier l'utilité, la supprimer si non nécessaire
     *
     * @param sourceNumber Numéro (ID) de la station destination
     */
    public void setDestination(int destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    /**
     * Fonction simulant le parcourt d'une frame sur le support
     *
     * @param frame
     * @return
     */
    private Frame transfert(Frame frame) {
        // Latence (fichier de paramètres)
        try {
            Support.sleep(this.sDelay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (typeError != NOERROR) {
            errorCounter = (errorCounter + 1) % errorOccurence;
        }

        switch (typeError) {
            case NOERROR:
                break;
            case CORRUPTION:
                if (errorCounter == 0) {
                    System.out.println("Frame : " + frame.getFrameNumber() + "  Data :");
                    int frameNumber = frame.getFrameNumber();
                    int numberOfFrames = frame.getNumberOfFrames();
                    frame.readData();
                    int i;
                    byte[] rawData = frame.getData();
                    boolean[] data = Hamming.byteToBitArray(frame.getData());
                    for (int k = 0; k < rawData.length * 8; k++) {
                        if (Math.random() < 0.5) {
                            data[k] = !data[k];
                        }
                    }
                    byte[] newData = Hamming.bitToByteArray(data);
                    frame = new Frame(rawData.length, newData, true);
                    frame.setFrameNumber(frameNumber);
                    frame.setNumberOfFrames(numberOfFrames);
                }
                break;
            case LOSTFRAME:
                if (errorCounter == 0) {
                    frame = null;
                }
                break;
        }

        return frame;
    }

    /**
     * Fonction principale d'exécution du thread.
     *
     * TODO - Erreurs, latence et optimisation du processeur
     */
    @Override
    public void run() {
        while (true) {
            Frame frameToSend;
            synchronized (System.out) {
                if (!readyToSendSource && !dataReceivedAtDest && sendSource != null) {
                    System.out.println("Sending data frame " + sendSource.getFrameNumber() + " at destination.");
                    frameToSend = transfert(sendSource);
                    receivedAtDestination = frameToSend;
                    if (frameToSend == null) {
                        System.out.println("ERROR : Data frame was lost");
                    } else {
                        System.out.println("Data frame " + frameToSend.getFrameNumber() + " was received at destination.");
                    }
                    readyToSendSource = true;
                    dataReceivedAtDest = true;
                }
                System.out.flush();
            }

            synchronized (System.out) {
                // Vérifie si la destination attend pour envoyer une trame et que la source
                //   a traité sa dernière trame reçue.
                if (!readyToSendDestination && !dataReceivedAtSource && sendDestination != null) {
                    //synchronized (System.out) {
                    System.out.println("Sending acknowledgment for frame " + sendDestination.getFrameNumber() + " at source.");
                    //System.out.flush();
                    //}
                    frameToSend = transfert(sendDestination);
                    receivedAtSource = frameToSend;
                    if (frameToSend == null) {
                        System.out.println("ERROR : Acknowledgment was lost");
                    } else {
                        System.out.println("Acknowledgment for frame " + frameToSend.getFrameNumber() + " was received at source.");
                    }
                    readyToSendDestination = true;
                    dataReceivedAtSource = true;
                }
                System.out.flush();
            }
        }
    }

}
