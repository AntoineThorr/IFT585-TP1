/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Objet permettant de représenter une station d'émission et de réception de
      trames.
 */

public class Station extends Thread {

    // Le support de transmission utilisé par la station.
    private Support supportTransmission;
    
    // Le tampon d'envoi
    private Buffer sendBuffer;
    // Le tampon de réception
    private Buffer receiveBuffer;
    
    // Le path du fichier où l'on veut écrire le fichier reçu.
    // Ce n'est pas beau mais ça facilite les choses.
    private String outputDir;

    // Taille d'une trame
    private int frameSize;
    // Taille par défaut des buffers.
    private int bufferSize;
    
    // Numéro de la station permettant de s'identifier sur le support.
    private int stationNumber;

    
    // Constructeur
    public Station(Support supportTransmission, int bufferSize, int frameSize, int stationNumber) {
        this.supportTransmission = supportTransmission;
        sendBuffer = new Buffer(bufferSize);
        receiveBuffer = new Buffer(bufferSize);
        this.frameSize = frameSize;
        this.bufferSize = bufferSize;
        this.stationNumber = stationNumber;
    }

    // Fonction par défaut du thread. 
    //
    // TODO : Fait de l'attente active. C'est ce qu'il y a de plus simple mais ça
    //          fait tourner le processeur dans le vide. Ce n'est probablement pas
    //          très grave pour un travail de télématique par contre.
    //
    // TODO : Ce n'est pas le code le plus digeste... On peut sûrement l'améliorer.
    @Override
    public void run() {
        while (true) {
            // On vérifie si le support est disponible pour l'envoi et/ou si
            //   une trame a été reçu pour la station.
            boolean readyToSend, dataReceived;
            readyToSend = supportTransmission.isReadyToSend(stationNumber);
            dataReceived = supportTransmission.asReceivedData(stationNumber);
            
            // On vérifie si le support est prêt à envoyer et s'il y a quelque
            //   chose dans le tampon d'envoi.
            if (readyToSend && sendBuffer.isNotEmpty()) {
                // "next" va contenir la position dans le buffer d'envoi de la
                //   prochaine trame à envoyer. Retourne -1 s'il n'y en a pas à envoyer.
                // En théorie, si le buffer contient un élément et qu'il n'est pas à
                //   envoyer, c'est qu'on attend encore de recevoir un ACK/NACK.
                int next = sendBuffer.getNextToSend();
                if (next != -1) {
                    // On va chercher la trame dans le buffer.
                    Frame frameToSend = sendBuffer.getFrame(next);
                    // On envoie la trame sur le support.
                    supportTransmission.sendFrame(frameToSend, stationNumber);
                    // Si c'est un ACK/NACK, on le détruit
                    //
                    // TODO : Je crois qu'il faut en vrai le garder et le renvoyer
                    //          si nécessaire. On peut penser à une autre façon aussi
                    //          de faire le suivi des ACK/NACK.
                    if(!frameToSend.isData()){
                        sendBuffer.removeFrame(frameToSend.getFrameNumber());
                    }
                }
            }
            
            // On vérifie si une trame a été reçu et que le buffer de réception n'est pas 
            // plein.
            if (dataReceived && receiveBuffer.isNotFull()) {
                // On va chercher la trame reçue.
                Frame frameReceived = supportTransmission.retrieveData(stationNumber);
                // La fonction peut retourner NULL s'il y a une erreur.
                if (frameReceived != null) {
                    // On vérifie si c'est une trame de données.
                    if (frameReceived.isData()) /*Si c'est une trame de données */{
                        try {
                            // On conserve la trame de données dans le buffer de réception.
                            receiveFrame(frameReceived);
                        } catch (IOException ex) {
                            Logger.getLogger(Station.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else /* Si c'est un ACK/NACK */{
                        // On vérifie si c'est un ACK (TRUE) ou un NACK (FALSE)
                        if (frameReceived.frameWasReceived()) {
                            // On détruit la trame du buffer d'envoi si la destination
                            //   assure l'avoir reçue.
                            sendBuffer.removeFrame(frameReceived.getFrameNumber());
                        } else { // TODO : Si c'est un NACK, on doit renvoyer la trame.

                        }
                    }
                }
            }
        }
    }

    // Fonction appelée par l'utilisateur qui souhaite envoyer un fichier à
    //   la station de destination.
    public void sendFile(File file, int destination) throws IOException {
        //Diviser le fichier à envoyer en sections de données
        FrameFactory frameFactory = new FrameFactory(file, frameSize);
        
        // Pas vraiment nécessaire puisque c'est déjà défini au départ.
        supportTransmission.setSource(stationNumber);
        supportTransmission.setDestination(destination);
        
        int i = 0;
        int numberOfFrames = frameFactory.getNumberOfFrames();
        
        // Remplir le tampon d'envoi
        // Tant que toutes les trames n'ont pas toutes été envoyées, la fonction
        //   va vérifier si le tampon d'envoi est plein. Si ce n'est pas le cas,
        //   elle va y placer la prochaine trame.
        while (i < numberOfFrames) {
            if(sendBuffer.isNotFull()){
                sendBuffer.addFrame(frameFactory.getFrame(i));
                i++;
            }  
        }
    }
    
    // Fonction appelée quand la station reçoit une trame.
    //
    // 
    //
    // TODO : 
    private void receiveFrame(Frame frame) throws FileNotFoundException, IOException{
        // Initialise le buffer si c'est la première trame reçue.
        // Le buffer de réception va pouvoir contenir le nombre exacte de trame
        //   à recevoir pour un fichier. Beaucoup plus facile de cette façon.
        int numberOfFrames = frame.getNumberOfFrames();
        if (receiveBuffer.size() != numberOfFrames){
            receiveBuffer.initializeBuffer(numberOfFrames);
        }
        
        // Indique qu'il a reçu la trame (ACK)
        //
        // TODO : On doit vérifier avant si la trame reçue contient des erreurs et tenter
        //          de la corriger si le code correcteur est activé.
        sendAck(frame);
        
        // Insert la trame dans le buffer de réception à sa position
        //
        // TODO : En quelque sorte, je crois que j'ai fait par défaut le protocole 6.
        //          Je ne rejette pas de trame si je ne les reçoit pas dans l'ordre.
        //          Il faut rajouter la notion de rejet global et sélectif.
       receiveBuffer.addFrameAt(frame);

       // Si le buffer de réception est plein, on écrit dans le fichier.
        //
        // TODO : Encore une fois, c'est dans le cas du rejet sélectif. Pour le rejet 
        //          global, on peut sûrement écrire au fur et à mesure.
        if (!receiveBuffer.isNotFull()) {
            writeFile();
        }
    }

    // Fonction permettant d'écrire tout le fichier quand nous avons reçu toutes
    //   les trames. (Pour le rejet sélectif)
    //
    // De ce que j'ai cru comprendre, il est très difficile d'écrire dans le désordre
    //   dans un fichier. J'attend donc d'avoir tous les composants avant d'écrire.
    private void writeFile() throws FileNotFoundException, IOException {
        int size = receiveBuffer.size();
        FileOutputStream ops = new FileOutputStream(outputDir);
           OutputStreamWriter opsw = new OutputStreamWriter(ops);
           for(int i = 0 ; i < size ; i++){
               char[] data = receiveBuffer.getFrame(i).getData();
               opsw.write(data, 0, data.length);
           }
           opsw.close();
    }    
    
    // Fonction qui envoie un ACK
    //
    // La caractéristique "maison" d'un ACK est présentement un '1' au premier bit
    //   des données. Le numéro de trame du ACK est le numéro de la trame qu'on
    //   acknowledge. 
    //
    // La fonction essai d'envoyer tant qu'elle n'a pas réussi à placer la trame
    //   dans le buffer d'envoi.
    private void sendAck(Frame frame){
        char[] data = new char[1];
        data[0] = 1;
        Frame ackFrame = new Frame(frameSize, data, false);
        ackFrame.setFrameNumber(frame.getFrameNumber());
        ackFrame.setNumberOfFrames(frame.getNumberOfFrames());
        boolean sent;
        do{
            sent = sendBuffer.addFrame(ackFrame);
        } while(!sent);
    }
    
    // Fonction qui envoie un NACK
    //
    // La caractéristique "maison" d'un NACK est présentement un '0' au premier bit
    //   des données. Le numéro de trame du NACK est le numéro de la trame qu'on
    //   acknowledge. 
    //
    // La fonction essai d'envoyer tant qu'elle n'a pas réussi à placer la trame
    //   dans le buffer d'envoi.
    //
    // TODO : Les codes correcteurs et détecteurs n'ayant pas été faits, cette fonction
    //          n'a pas été testée. Il sera probablement plus logique de passer en 
    //          paramètre le numéro de la trame non-reçue que la trame non-reçue...
    private void sendNack(Frame frame){
        char[] data = new char[1];
        data[0] = 0;
        Frame nackFrame = new Frame(frameSize, data, false);
        boolean sent;
        do{
            sent = sendBuffer.addFrame(nackFrame);
        } while(!sent);
    }
    
    // Fonction appelée par l'utilisateur pour définir le path du fichier en output.
    public void setOutputDir(String outputDir){
        this.outputDir = outputDir;
    }
            
    // Fonction permettant d'obtenir le numéro de la station.
    public int getStationNumber(){
        return stationNumber;
    }
}
