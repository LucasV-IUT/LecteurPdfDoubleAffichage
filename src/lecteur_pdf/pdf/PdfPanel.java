/*
 * PdfPanel.java, 26/02/2022
 * IUT Rodez 2021-2022, INFO 2
 * Pas de copyright, aucun droits
 */

package lecteur_pdf.pdf;

import lecteur_pdf.GestionFenetre;
import lecteur_pdf.GestionMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Interface de la mainframe de l'application sans utilisation de forms
 *
 * @author Léo Franch
 * @author Lucas Vabre
 * @author Noé Villeneuve
 * @author Tristan Nogaret
 */
public class PdfPanel extends JPanel {

    /* Données */
    private int currentPage;
    private float scaleSizing;
    private float scaleZoom;
    private boolean pleineLargeur;
    private boolean processing;

    /* Chargeur de Pdf */
    private DocumentPdf pdfLoader;

    /* Interface */
    private final JTextField indexPageInput;
    private final JLabel maxPageLabel;
    private final JScrollPane scrollPane;
    private final JViewport viewport;
    private final JLabel page;

    /**
     * Crée une nouvelle interface de PDF vide
     */
    public PdfPanel() {
        super(new BorderLayout());

        scaleSizing = 0.0f;
        scaleZoom = 1.0f;
        currentPage = 0;
        processing = false;
        pleineLargeur = false;

        /* Contrôleurs */
        JPanel controls = new JPanel();
        JButton btnPrecedent = new JButton("Précédent");
        indexPageInput = new JTextField(7);
        indexPageInput.setText("-");
        maxPageLabel = new JLabel("/ -");
        JButton btnSuivant = new JButton("Suivant");

        controls.add(btnPrecedent);
        controls.add(indexPageInput);
        controls.add(maxPageLabel);
        controls.add(btnSuivant);

        add(controls, BorderLayout.PAGE_START);

        /* View */
        page = new JLabel();
        JPanel pagePanel = new JPanel();
        /* Contenu de View */
        scrollPane = new JScrollPane(pagePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        viewport = scrollPane.getViewport();

        pagePanel.add(page);

        add(scrollPane, BorderLayout.CENTER);

        /* Actions */
        btnSuivant.addActionListener(this::btnSuivantAction);
        btnPrecedent.addActionListener(this::btnPrecedentAction);

        /* Saisie uniquement de caractère numérique */
        indexPageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String value = indexPageInput.getText();
                int l = value.length();
                indexPageInput.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9');
            }
        });

        /* À la pression de la touche entrée on fait une recherche */
        indexPageInput.addActionListener(e -> {
            String saisie = indexPageInput.getText();
            try {
                int index = Integer.parseInt(saisie);
                if (isPageValide(index - 1)) setPage(index - 1);
                else throw new Exception();
            } catch (Exception f) {
                indexPageInput.setText(Integer.toString(currentPage +1));
            }

        });

        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resize();
            }
        });
    }

    /**
     * @param evt Écouteur d'évènement
     */
    private void btnSuivantAction(ActionEvent evt) {
        if (GestionMode.isModeSepare()) nextPage();
        else GestionFenetre.nextPages();
    }

    /**
     * @param evt Écouteur d'évènement
     */
    private void btnPrecedentAction(ActionEvent evt) {
        if (GestionMode.isModeSepare()) previousPage();
        else GestionFenetre.previousPages();
    }

    /**
     * TODO
     */
    public void resize() {
        if (pdfLoader == null || processing) return;

        if (pleineLargeur) {
            float viewportWidth = viewport.getWidth();
            float scrollpaneWidth = scrollPane.getVerticalScrollBar().getWidth();
            float pdfMinWidth = pdfLoader.getMinWidth();

            scaleSizing = (viewportWidth - scrollpaneWidth) / pdfMinWidth - scaleZoom;
        } else {
            float viewportHeight = viewport.getHeight();
            float scrollpaneHeight = scrollPane.getVerticalScrollBar().getHeight();
            float pdfMinHeight = pdfLoader.getMinHeight();

            scaleSizing = (viewportHeight - scrollpaneHeight) / pdfMinHeight - scaleZoom;
        }
        updateScaleSizing(scaleSizing);
    }

    /**
     * @param pleineLargeur booléen qui défini l'état du mode pleine largeur
     */
    public void setPleineLargeur(boolean pleineLargeur) {
        this.pleineLargeur = pleineLargeur;
        resize();
    }

    /**
     * Prédicat qui vérifie si un index de page est valide pour le PDF courant
     *
     * @param index Entier correspondant a l'indice du numéro de page à tester
     * @return true si le prédicat est vérifié, false sinon
     */
    private boolean isPageValide(int index) {
        return pdfLoader != null
                && 0 <= index
                && index < pdfLoader.getNbPages();
    }

    /**
     * Methode qui permet de charger un PDF dans la fenêtre courante
     *
     * @param pdfFile Le fichier PDF à charger
     * @return true si le PDF a pu se charger, false sinon
     */
    public boolean chargerPdf(File pdfFile) {
        try {
            setPdfLoader(new DocumentPdf(pdfFile));
            setPage(0);
            return true;
        } catch (IOException ignored) {}
        return false;
    }

    /**
     * @param pdfLoader un nouveau document PDF à affecter à la fenêtre
     */
    public void setPdfLoader(DocumentPdf pdfLoader) {
        this.pdfLoader = pdfLoader;
    }

    /**
     * Décharge le document courant s'il y en a un
     */
    public void dechargerPdf() {
        if (!isCharge()) return;

        /* Ferme le loader et l'efface */
        pdfLoader.close();
        pdfLoader = null;

        /* Efface l'image de la page */
        page.setIcon(null);
        currentPage = 0;

        /* Interface Vide */
        indexPageInput.setText("");
        maxPageLabel.setText("/ -");

        /* Efface les données relatives au zoom */
        scaleSizing = 0.0f;
        scaleZoom = 1.0f;

        validate();
    }

    /**
     * Change la taille de la page courante
     *
     * @param scale Valeur flottante (1.00f == 100%)
     */
    public void updateScaleZoom(float scale) {
        scaleZoom = scale;
        setPage(currentPage);
    }

    /**
     * Change la taille de la page courante
     *
     * @param scale Valeur flottante (1.00f == 100%)
     */
    private void updateScaleSizing(float scale) {
        scaleSizing = scale;
        setPage(currentPage);
    }

    /**
     * Affiche la page suivante
     */
    public void nextPage() {
        setPage(currentPage + 1);
    }

    /**
     * Affiche la page suivante
     */
    public void previousPage() {
        setPage(currentPage - 1);
    }

    /**
     * Essaye de changer de page si possible, sinon ne fait rien
     *
     * @param index Le numéro de la page où l'on veut se rendre
     */
    private void setPage(int index) {
        if (!isPageValide(index)) return;

        processing = true;
        try {
            page.setIcon(new ImageIcon(pdfLoader.renderPage(index, scaleZoom + scaleSizing)));
            currentPage = index;
            indexPageInput.setText(Integer.toString(currentPage + 1));
            maxPageLabel.setText(String.format("/%d", pdfLoader.getNbPages()));
        } catch (IOException ignored) {}
        processing = false;
    }

    /**
     * Prédicat qui vérifie si un PDF est chargé ou non
     * @return true si un PDF est chargé, false sinon
     */
    public boolean isCharge() {
        return pdfLoader != null;
    }
}
