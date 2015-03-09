package Protocole;

/**
 * Objet représentant une trame.
 *
 * TODO : L'objet que j'ai créé n'est pas très représentatif de ce que devrait
 * être une trame de ce que j'en comprend. J'ai ajouté des variables pour me
 * simplifier la vie. Si on doit avoir une représentation plutôt fidèle d'une
 * vrai trame, il va falloir apporter des modifications.
 *
 */
public class Frame {

    // Numéro de la trame. Permet le ré-ordonnancement.
    private int frameNumber;

    // Nombre de trames en tout pour le fichier.
    private int numberOfFrames;

    // Type de trame.
    private boolean type;       //0 = Acknowledgment   ;   1 = Data

    // Les données.
    private byte[] data;

    // Nombre de bytes de données (sans code)
    private int frameSize;

    // TODO : Pour la validation.
    private int totalControle;

    /**
     * Constructeur d'une trame.
     *
     * @param frameSize Taille des données utiles (sans le code vérificateur)
     * @param data      Données incluant le code de Hamming
     * @param isData    TRUE si données, FALSE si ACK/NAK
     */
    public Frame(int frameSize, byte[] data, boolean isData) {
        this.frameSize = frameSize;
        this.data = data;
        type = isData;      // 
    }

    /**
     * Permet de définir le numéro de la trame. Appelée par le FrameFactory.
     *
     * @param frameNumber Numéro voulu pour la trame
     */
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    /**
     * Permet de définir le nombre total de trames. Appelée par le FrameFactory.
     *
     * TODO - Trouver un moyen de faire sans.
     *
     * @param numberOfFrames Nombre total de frames du fichier
     */
    public void setNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    /**
     * Fonction permettant d'afficher le contenu d'une trame. Il faut en faire
     * une String afin d'éviter l'affichage caractère par caractère. Utile au
     * début pour le debugging. Pourrait être repris pour l'affichage sur la
     * console (avec quelques modifications).
     */
    public void readData() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            result.append(data[i]);
        }

        System.out.println(result);
    }

    /**
     * Fonction retournant les données de la trame.
     *
     * @return Tableau des données de la trame au format byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Fonction retournant le numéro de la trame.
     *
     * @return Numéro de la trame au format int
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Fonction retournant le nombre de trame dans la suite de trame pour le
     * fichier.
     *
     * @return Numbre total de trames du fichier au format int
     */
    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    /**
     * Fonction permettant de savoir si la trame contient des données (TRUE) ou
     * un acknowledgment (FALSE)
     *
     * @return TRUE si la trame contient des données, FALSE si ACK/NAK
     */
    public boolean isData() {
        return type;
    }

    /**
     * Fonction permettant de vérifier, dans le cas d'un acknowledgment, si
     * c'est un ACK (TRUE) ou un NAK (FALSE). La convention "maison" est qu'un 0
     * au début des données indique un NACK et un 1 (n'importe quoi d'autre dans
     * les faits) est un ACK.
     *
     * TODO - Gérer les exceptions
     *
     * @return TRUE si ACK, FALSE si NAK
     */
    public boolean frameWasReceived() {
        return data[0] != 0;
    }
}
