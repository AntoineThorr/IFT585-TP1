/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

/*
 Objet permettant de diviser un fichier en une série de trames.
 */
public class FrameFactory {

    // Taille d'une trame
    private int frameSize;

    // Liste contenant des blocks égaux de données
    private ArrayList<Frame> dataBlocks;

    // Nombre de trame nécessaire pour contenir le fichier.
    private int frameCount = 0;

    // Constructeur
    // On divise le fichier en trames dès la création de la Factory.
    // Il suffit par la suite de lui demander une trame pour le fichier.
    public FrameFactory(File file, int frameSize) throws IOException {
        // Contient en ordre tous les blocks de données créés à partir du fichier
        this.dataBlocks = new ArrayList<>();

        // Taille d'une trame tel que définie dans les propriétés.
        this.frameSize = frameSize;

        //Création du tableau total d'octets du fichier
        byte[] data = readFile(file);
        //Itération tant que reste des octets à lire
        while (data.length - this.frameCount * this.frameSize > 0) {
            //Ajout à l'arrayList de chaque frame à partir du tableau d'octets et du compteur de frames
            this.dataBlocks.add(this.genFrame(data));
        }

//        try {
//            InputStream ips = new FileInputStream(file);
//            InputStreamReader ipsr=new InputStreamReader(ips);
//            BufferedReader br=new BufferedReader(ipsr);
//            
//            // On lit caractère par caractère le fichier.
//            int character = br.read();
//            // -1 indique que je suis arrivé à la fin.
//            while(character != -1){
//                // On crée un nouveau block de donnée qui sera compris dans une trame.
//                char[] dataBlock = new char[frameSize];
//                // On rempli le block caractère par caractère.
//                int i;
//                for (i = 0 ; character != -1 && i < frameSize; i++){
//                    dataBlock[i] = (char) character;
//                    character = br.read();
//                }
//                // On fait du remplissage s'il reste encore de l'espace à la fin.
//                // TODO : Il serait mieux d'ajouter un genre de END OF FILE au début
//                //          de la fin. Si on ajoute cela, il faudra en tenir compte
//                //          lors de l'écriture du fichier.
//                if (character == -1 && i < frameSize){
//                    for(int j = i ; j < frameSize ; j ++){
//                        dataBlock[j] = (char) ' ';
//                    }
//                }
//                // On ajoute le block de donnée dans la liste.
//                dataBlocks.add(dataBlock);
//                // On incrémente la quantité de trame du fichier.
//                frameCount++;
//            }
//            
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(FrameFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private byte[] readFile(File f) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(f.toPath()); //Enregistrement dans le format "byte" de Java : de -128 à 127
            for (int i = 0; i < data.length; i++) {
                //System.out.println(i + " -> " + data[i]);
            }
            //System.out.println(new String(data, "UTF-8")); //Debug : affichage console reconstitué
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
        int start = this.frameCount * this.frameSize;
        int stop = (this.frameCount + 1) * this.frameSize;
        byte[] frameData = Arrays.copyOfRange(data, start, stop);

        //TODO - ajouter code de Hamming
        //Hamming.code(frameData);
        Frame f = new Frame(this.frameSize, frameData, true);
        this.frameCount++;
        return f;
    }

    // Fonction permettant d'obtenir une trame spécifique à partir de son numéro
    //
    // J'ajoute la quantité de trame et le numéro de la trame pour me faciliter 
    //   la vie.
    public Frame getFrame(int frameNumber) {
//        Frame frame = new Frame(frameSize, (byte[]) dataBlocks.get(frameNumber), true);
//        frame.setFrameNumber(frameNumber);
//        frame.setNumberOfFrames(frameCount);
//        return frame;
        return this.dataBlocks.get(frameNumber);
    }

    // Retourne le nombre de trame pour le fichier
    public int getNumberOfFrames() {
        return this.frameCount;
    }

    // Retourne la taille d'une trame
    public int getFrameSize() {
        return this.frameSize;
    }
}
