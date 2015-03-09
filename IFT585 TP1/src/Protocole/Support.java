/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Objet permettant de représenter un support de transmission.

 TODO : La création des erreurs et la latence, entre autre, ne sont pas encore faits.
 */
public class Support extends Thread {
    // Variables pour l'envoi de messages de la source à la destination

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

    // Afin de se simplifier la vie, le support est unidirectionnel.
    private int sourceNumber = 0;
    private int destinationNumber = 0;

    // Type d'erreur
    private int typeError;
    final static private int NOERROR = 0;
    final static private int CORRUPTION = 1;
    final static private int LOSTFRAME = 2;

    private int errorCounter;
    final static private int errorOccurence = 10;

    // Constructeur
    // Par défaut, on décide qu'une station sera la source et l'autre la destination.
    public Support(int sourceNumber, int destinationNumber, int error) {
        this.sourceNumber = sourceNumber;
        this.destinationNumber = destinationNumber;
        readyToSendSource = readyToSendDestination = true;
        dataReceivedAtDest = dataReceivedAtSource = false;
        this.typeError = error;
        this.errorCounter = 0;
    }

    // Fonction appelée par une station afin de savoir si elle peut envoyer
    //   une trame sur le support.
    public synchronized boolean isReadyToSend(int stationNumber) {
        if (stationNumber == sourceNumber) {
            return readyToSendSource;
        } else if (stationNumber == destinationNumber) {
            return readyToSendDestination;
        }
        return false;
    }

    // Fonction appelée par une station afin de savoir si elle a reçu une trame.
    public synchronized boolean asReceivedData(int stationNumber) {
        if (stationNumber == destinationNumber) {
            return dataReceivedAtDest;
        } else if (stationNumber == sourceNumber) {
            return dataReceivedAtSource;
        }
        return false;
    }

    // Fonction appelée par une station pour envoyer une trame sur le support.
    //
    // TODO : Nous pourrions ajouter des protections. La fonction peut être appelée
    //          même si le support est "occupé".
    public synchronized void sendFrame(Frame frame, int stationNumber) {
        if (stationNumber == sourceNumber) {
            readyToSendSource = false;
            sendSource = frame;
        } else if (stationNumber == destinationNumber) {
            readyToSendDestination = false;
            sendDestination = frame;
        }
    }

    // Fonction appelée par une station afin de récupérer une trame reçue.
    //
    // TODO : Nous pourrions ajouter des protections. La fonction peut être appelée
    //          même si le support n'a rien reçu.
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

    // Fonction permettant de redéfinir la source. N'est plus vraiment nécessaire.
    public void setSource(int sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    // Fonction permettant de redéfinir la destination. N'est plus vraiment nécessaire.
    public void setDestination(int destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    private Frame transfert(Frame frame) {
        // Latence (default = 100 ? instancier depuis param ?)
        try {
            Support.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (typeError != NOERROR) {
            errorCounter = (errorCounter + 1) % errorOccurence;
            //System.out.println(errorCounter);
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
                    Hamming hamming = new Hamming();
                    int i;
                    byte[] rawData = frame.getData();
                    boolean[] data = new boolean[rawData.length * 8];
                    data = hamming.byteToBitArray(frame.getData());
                    for (int k = 0; k < rawData.length * 8; k++) {
                        if (Math.random() < 0.5) {
                            data[k] = !data[k];
                        }
                    }
                    byte[] newData = hamming.bitToByteArray(data);
                    frame = new Frame(rawData.length, newData, true);
                    frame.setFrameNumber(frameNumber);
                    frame.setNumberOfFrames(numberOfFrames);
                }
                break;
            case LOSTFRAME:
                if (errorCounter == 0) {
//                    System.out.println("Frame : " + frame.getFrameNumber() + "  Data :");
//                    frame.readData();
                    frame = null;
                }
                break;
        }

        return frame;
    }

    // Fonction du thread.
    //
    // TODO : Fait de l'attente active. C'est ce qu'il y a de plus simple mais ça
    //          fait tourner le processeur dans le vide. Ce n'est probablement pas
    //          très grave pour un travail de télématique par contre.
    //
    // TODO : Il manque la simulation de la latence et l'introduction d'erreur.
    @Override
    public void run() {
        while (true) {
            // Ça ne fonctionne pas sans le sleep...
            //
            // TODO : Élucider le mystère du sleep. Sans lui, plus rien ne fonctionne.

            // Vérifie si la source attend pour envoyer une trame et que la destination
            //   a traité sa dernière trame reçue.
//            try {
//                Support.sleep(100);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
//            }
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
