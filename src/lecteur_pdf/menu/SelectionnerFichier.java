/*
 * SelectionnerFichier.java, 18/11/2021
 * IUT Rodez 2021-2021, INFO2
 * pas de copyright, aucun droits
 */
package lecteur_pdf.menu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
/**
 * Classe qui permet d'ouvrir une fenêtre pour sélectionner le fichier pdf à
 * ouvrir
 * @author Léo FRANCH
 * @author Tristan NOGARET
 * @author Lucàs VABRE
 * @author Noé VILLENEUVE
 * @version  1.0
 */
public class SelectionnerFichier {

    /**
     * Méthode qui créée une fenêtre pour que l'utilisateur choisisse un
     * fichier PDF
     * @return file le fichier choisi par l'utilisateur
     */
    public static File ouvrirFichier() {

        JFileChooser fileChooser = new JFileChooser
                     (FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle("Sélectionnez un PDF");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter
            filter = new FileNameExtensionFilter("PDF files (*.pdf)", "pdf");
        fileChooser.addChoosableFileFilter(filter);

        File file;
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = new File(fileChooser.getSelectedFile().getPath());
        } else {
          file = null;
        }
        return file;
    }
}
