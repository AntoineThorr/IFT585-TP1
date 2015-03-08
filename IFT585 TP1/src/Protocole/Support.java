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

public class Support extends Thread{
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
    
    
    // Constructeur
    // Par défaut, on décide qu'une station sera la source et l'autre la destination.
    public Support(int sourceNumber, int destinationNumber){
        this.sourceNumber = sourceNumber;
        this.destinationNumber = destinationNumber;
        readyToSendSource = readyToSendDestination = true;
        dataReceivedAtDest = dataReceivedAtSource = false;
    }
    
    // Fonction appelée par une station afin de savoir si elle peut envoyer
    //   une trame sur le support.
    public synchronized boolean isReadyToSend(int stationNumber){
        if(stationNumber == sourceNumber){
            return readyToSendSource;
        } else if (stationNumber == destinationNumber){
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
    public void setSource(int sourceNumber){
        this.sourceNumber = sourceNumber;
    }
    
    // Fonction permettant de redéfinir la destination. N'est plus vraiment nécessaire.
    public void setDestination(int destinationNumber){
        this.destinationNumber = destinationNumber;
    }
    
    // Fonction du thread.
    //
    // TODO : Fait de l'attente active. C'est ce qu'il y a de plus simple mais ça
    //          fait tourner le processeur dans le vide. Ce n'est probablement pas
    //          très grave pour un travail de télématique par contre.
    //
    // TODO : Il manque la simulation de la latence et l'introduction d'erreur.
    @Override
    public void run(){
        while(true){
            // Ça ne fonctionne pas sans le sleep...
            //
            // TODO : Élucider le mystère du sleep. Sans lui, plus rien ne fonctionne.
            try {
                Support.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
            }      
            
            // Vérifie si la source attend pour envoyer une trame et que la destination
            //   a traité sa dernière trame reçue.
            if(!readyToSendSource && !dataReceivedAtDest){
                receivedAtDestination = sendSource;
                readyToSendSource = true;
                dataReceivedAtDest = true;
            } 
            
            // Vérifie si la destination attend pour envoyer une trame et que la source
            //   a traité sa dernière trame reçue.
            if(!readyToSendDestination && !dataReceivedAtSource){
                receivedAtSource = sendDestination;
                readyToSendDestination = true;
                dataReceivedAtSource = true;
            }
        }
    }
    
}
