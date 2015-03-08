/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.util.Arrays;

/*
    Objet représentant une trame.

TODO : L'objet que j'ai créé n'est pas très représentatif de ce que devrait être 
        une trame de ce que j'en comprend. J'ai ajouté des variables pour me simplifier
        la vie. Si on doit avoir une représentation plutôt fidèle d'une vrai trame,
        il va falloir apporter des modifications.

TODO : Il manque les algorithmes permettant de rajouter le code détecteur et le code
        correcteur (Hamming).
 */


public class Frame {
    // Numéro de la trame. Permet le ré-ordonnancement.
    private int frameNumber;
    
    // Nombre de trame en tout pour le fichier.
    private int numberOfFrames;
    
    // Type de trame.
    private boolean type;       //0 = Acknowledgment   ;   1 = Data
    
    // Les données
    private byte[] data;
    
    // TODO : Pour la validation.
    private int totalControle;
    
    
    // Constructeur d'une trame. 
     public Frame(int frameSize, byte[] data, boolean isData){
        this.data = new byte[frameSize];
        this.data = data;
        type = isData;      // TRUE si des données, FALSE si acknowledgment
     }
     
     // Permet de définir le numéro de la trame. Appelée par le FrameFactory.
     public void setFrameNumber(int frameNumber){
         this.frameNumber = frameNumber;
     }
     
     // Permet de définir le nombre total de trames. Appelée par le FrameFactory.
     public void setNumberOfFrames(int numberOfFrames){
         this.numberOfFrames = numberOfFrames;
     }
     
     // Fonction permettant d'afficher le contenu d'une trame.
     //
     // Il faut en faire une String afin d'éviter l'affichage caractère par caractère.
     // Utile au début pour le debugging. Pourrait être repris pour l'affichage
     //   sur la console; avec quelques modifications.
     public void readData(){
         StringBuffer result = new StringBuffer();
         for (int i = 0; i < data.length; i++) {
             result.append(data[i]);
         }
  
         //System.out.println(result);
     }

     // Fonction retournant les données de la trame.
    public byte[] getData() {
        return data;
     }
    
    // Fonction retournant le numéro de la trame.
    public int getFrameNumber(){
        return frameNumber;
    }
    
    // Fonction retournant le nombre de trame dans la suite de trame pour le fichier.
    public int getNumberOfFrames(){
        return numberOfFrames;
    }
    
    // Fonction permettant de savoir si la trame contient des données (TRUE)
    //   ou un acknowledgment (FALSE)
    public boolean isData(){
        return type;
    }

    // Fonction permettant de vérifier, dans le cas d'un acknowledgment, si c'est 
    //   un ACK (TRUE) ou un NACK (FALSE)
    //
    // La convention maison est qu'un 0 au début des données indique un NACK et 
    //   un 1 (n'importe quoi d'autre dans les faits) est un ACK.
    //
    // TODO : La fonction devrait avoir un peu plus de protection.
    public boolean frameWasReceived(){
        return data[0] != 0;
    }
}
