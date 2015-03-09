package Protocole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/*
 Objet permettant de représenter une station d'émission et de réception de
 trames.
 */

/**
 *
 * @author Antoine
 */

public class Station extends Thread {

    // Le support de transmission utilisé par la station.
    private Support supportTransmission;

    // Le tampon d'envoi
    private Buffer sendBuffer;

    // Le tampon de réception
    private Buffer receiveBuffer;

    // Le path du fichier où l'on veut écrire le fichier reçu.
    private String outputDir;

    // Taille d'une trame
    private int frameSize;

    // Taille par défaut des buffers.
    private int bufferSize;

    // Numéro de la station permettant de s'identifier sur le support.
    private int stationNumber;

    // Délai de temporisation (durée du timer du buffer)
    private int tempo;

    //Timers gérant l'expiration des frames envoyées (remplace le tick maison)
    Timer[] sTimers;

    /**
     * Constructeur de la classe Station
     *
     * @param supportTransmission Support choisi pour la transmission
     * @param bufferSize          Taille du buffer en int
     * @param stationNumber       Numéro désigné de la station (ID)
     * @param frameSize           Taille des trames à transmettre (données
     *                            utiles)
     */
    public Station(Support supportTransmission, int bufferSize, int frameSize, int stationNumber) {
        this.supportTransmission = supportTransmission;
        sendBuffer = new Buffer(bufferSize);
        receiveBuffer = new Buffer(bufferSize);
        this.frameSize = frameSize;
        this.bufferSize = bufferSize;
        this.stationNumber = stationNumber;
    }

    /**
     * Fonction par défaut du thread. Fait de l'attente active. C'est ce qu'il y
     * a de plus simple mais ça fait tourner le processeur dans le vide. Ce
     * n'est probablement pas très grave pour un travail de télématique par
     * contre.
     *
     * TODO - Améliorer le code...
     */
    @Override
    public void run() {
        while (true) {
            /*
             * On vérifie si le support est disponible pour l'envoi et/ou si une
             * trame a été reçue pour la station. On vérifie si le support est
             * prêt à envoyer et s'il y a quelque chose dans le tampon d'envoi.
             */
            if (supportTransmission.isReadyToSend(stationNumber) && sendBuffer.isNotEmpty()) {
                /*
                 * "next" va contenir la position dans le buffer d'envoi de la
                 * prochaine trame à envoyer. Retourne -1 s'il n'y en a pas à
                 * envoyer. En théorie, si le buffer contient un élément et
                 * qu'il n'est pas à envoyer, c'est qu'on attend encore de
                 * recevoir un ACK/NACK.
                 */
                int next = sendBuffer.getNextToSend();
                if (next != -1) {
                    // On va chercher la trame dans le buffer.
                    Frame frameToSend = sendBuffer.getFrame(next);

                    // On envoie la trame sur le support.
                    supportTransmission.sendFrame(frameToSend, stationNumber);
                    /*
                     * Si c'est un ACK/NACK, on le détruit
                     *
                     * TODO : Je crois qu'il faut en vrai le garder et le
                     * renvoyer si nécessaire. On peut penser à une autre façon
                     * aussi de faire le suivi des ACK/NACK.
                     */
                    if (!frameToSend.isData()) {
                        sendBuffer.removeFrame(frameToSend.getFrameNumber());
                    }
                }
            }

            /**
             * On vérifie si une trame a été reçu et que le buffer de réception
             * n'est pas plein
             */
            if (supportTransmission.asReceivedData(stationNumber) && receiveBuffer.isNotFull()) {
                // On va chercher la trame reçue.
                Frame frameReceived = supportTransmission.retrieveData(stationNumber);
                // La fonction peut retourner NULL s'il y a une erreur.
                if (frameReceived != null) {
                    // On vérifie si c'est une trame de données.
                    if (frameReceived.isData()) /*Si c'est une trame de données */ {
                        receiveFrame(frameReceived);
                    } else /* Si c'est un ACK/NAK */ {
                        // On vérifie si c'est un ACK (TRUE) ou un NAK (FALSE)
                        if (frameReceived.frameWasReceived()) {
                            // On détruit la trame du buffer d'envoi si la destination assure l'avoir reçue.
                            sendBuffer.removeFrame(frameReceived.getFrameNumber());
                        } else { // TODO : Si c'est un NAK, on doit renvoyer la trame.

                        }
                    }
                }
            }
        }
    }

    // 
    /**
     * Fonction appelée par l'utilisateur qui souhaite envoyer un fichier à la
     * station de destination.
     *
     * @param file        Fichier à envoyer
     * @param destination Indicatif de la station de destination
     */
    public void sendFile(File file, int destination) {
        //Division le fichier à envoyer en sections de données
        FrameFactory frameFactory = new FrameFactory(file, frameSize);

        int numberOfFrames = frameFactory.getNumberOfFrames();
        sTimers = new Timer[numberOfFrames];

        /**
         * Remplir le tampon d'envoi. Tant que toutes les trames n'ont pas
         * toutes été envoyées, la fonction va vérifier si le tampon d'envoi est
         * plein. Si ce n'est pas le cas, elle va y placer la prochaine trame.
         */
        int i = 0;
        while (i < numberOfFrames) {
            if (sendBuffer.isNotFull()) {
                sendBuffer.addFrame(frameFactory.getFrame(i));
                sTimers[i] = new Timer();
                sTimers[i].schedule(new TimerTask() {
                    public void run() {
                        //TODO - Gérer l'expiration de la trame
                    }
                }, tempo);
                i++;
            }
        }
    }

    /**
     * Fonction appelée quand la station reçoit une trame.
     *
     * @param frame Frame reçue par la station
     */
    private void receiveFrame(Frame frame) {
        /**
         * Initialise le buffer si c'est la première trame reçue. Le buffer de
         * réception va pouvoir contenir le nombre exacte de trame à recevoir
         * pour un fichier. Beaucoup plus facile de cette façon.
         */
        int numberOfFrames = frame.getNumberOfFrames();
        if (receiveBuffer.getSize() != numberOfFrames) {
            receiveBuffer.initializeBuffer(numberOfFrames);
        }

        /**
         * Indique qu'il a reçu la trame (ACK)
         *
         * TODO : On doit vérifier avant si la trame reçue contient des erreurs
         * et tenter de la corriger si le code correcteur est activé.
         */
        sendAck(frame);

        /**
         * Insert la trame dans le buffer de réception à sa position
         *
         * TODO - Rajouter la notion de rejet global et sélectif.
         */
        receiveBuffer.addFrameAt(frame);

        /**
         * Si le buffer de réception est plein, on écrit dans le fichier.
         *
         * TODO : Encore une fois, c'est dans le cas du rejet sélectif. Pour le
         * rejet global, on peut sûrement écrire au fur et à mesure.
         */
        if (!receiveBuffer.isNotFull()) {
            writeFile();
        }
    }

    /**
     * Fonction d'écriture des trames reçues à la station de réception dans le
     * fichier précédemment spécifié.
     */
    private void writeFile() {
        try {
            FileOutputStream ops = new FileOutputStream(this.outputDir);

            //Ecriture de toutes les trames sans la dernière
            for (int i = 0; i < this.receiveBuffer.getSize() - 1; i++) {
                byte[] data = receiveBuffer.getFrame(i).getData();
                ops.write(data, 0, data.length);
            }
            byte[] data = receiveBuffer.getFrame(this.receiveBuffer.getSize() - 1).getData();

            //Ecriture de la dernière trame, avec nettoyage des octets de remplissage
            int eof = Arrays.asList(ArrayUtils.toObject(data)).indexOf(new Byte("0"));
            ops.write(data, 0, eof);
            ops.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Fonction qui envoie un ACK
     *
     * La caractéristique "maison" d'un ACK est présentement un '1' au premier
     * bit des données. Le numéro de trame du ACK est le numéro de la trame
     * qu'on acknowledge.
     *
     * La fonction essai d'envoyer tant qu'elle n'a pas réussi à placer la trame
     * dans le buffer d'envoi
     *
     * @param frame Envoie l'ACK correspondant à la frame reçue
     */
    private void sendAck(Frame frame) {
        byte[] data = new byte[1];
        data[0] = 1;
        Frame ackFrame = new Frame(frameSize, data, false);
        ackFrame.setFrameNumber(frame.getFrameNumber());
        ackFrame.setNumberOfFrames(frame.getNumberOfFrames());
        boolean sent;
        do {
            sent = sendBuffer.addFrame(ackFrame);
        } while (!sent);
    }

    /**
     * Fonction qui envoie un NAK
     *
     * La caractéristique "maison" d'un NAK est présentement un '0' au premier
     * bit des données. Le numéro de trame du NAK est le numéro de la trame
     * qu'on acknowledge.
     *
     * La fonction essai d'envoyer tant qu'elle n'a pas réussi à placer la trame
     * dans le buffer d'envoi.
     *
     * TODO - Les codes correcteurs et détecteurs n'ayant pas pu être mis en
     * service, cette fonction n'a pas été testée
     */
    private void sendNak(int numTrameNAK) {
        byte[] data = new byte[2];
        data[0] = 0;
        data[0] = 0;
        data[1] = Byte.valueOf(String.valueOf(numTrameNAK));
        Frame nakFrame = new Frame(frameSize, data, false);
        boolean sent;
        do {
            sent = sendBuffer.addFrame(nakFrame);
        } while (!sent);
    }

    /**
     * Fonction appelée par l'utilisateur pour définir le path du fichier en
     * output.
     *
     * @param outputDir Chemin du fichier de sortie
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Fonction appelée par l'utilisateur pour définir le délai de temporisation
     * des trames envoyées.
     *
     * @param t Délai de temporisation prévu
     */
    public void setTempo(int t) {
        this.tempo = t;
    }

    /**
     * Fonction permettant d'obtenir le numéro de la station.
     *
     * @return Le numéro de la station (ID)
     */
    public int getStationNumber() {
        return stationNumber;
    }
}
