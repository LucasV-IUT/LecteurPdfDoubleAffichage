/*
 * PdfPanel.java, 26/02/2022
 * IUT Rodez 2021-2022, INFO 2
 * Pas de copyright, aucun droits
 */

package lecteur_pdf.pdf;

import lecteur_pdf.GestionMode;
import lecteur_pdf.GestionPdf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    private int currentPage;

    private float scaleSizing;
    private float scaleZoom;

    private boolean pleineLargeur;

    private PdfLoader pdfLoader;

    private final JTextField indexPageInput;
    private final JLabel maxPageLabel;

    private final JScrollPane scrollPane;
    private final JViewport viewport;
    private final JLabel page;

    private boolean processing;

    public PdfPanel() {
        super(new BorderLayout());

        scaleSizing = 0.0f;
        scaleZoom = 1.0f;
        currentPage = 0;
        processing = false;
        pleineLargeur = false;

        /* Controls */
        JPanel controls = new JPanel();
        /* Contenu de Controls */
        JButton btnPrecedent = new JButton("Précédent");
        indexPageInput = new JTextField();
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
        btnSuivant.addActionListener(e -> {
            if (GestionMode.isModeSepare()) nextPage();
            else GestionPdf.nextPages();
        });

        btnPrecedent.addActionListener(e -> {
            if (GestionMode.isModeSepare()) previousPage();
            else GestionPdf.previousPages();
        });

        indexPageInput.addActionListener(e -> {
            String saisie = indexPageInput.getText();
            try {
                int index = Integer.parseInt(saisie);
                if (isPageValide(index - 1)) setPage(index - 1);
                else throw new Exception();
            } catch (Exception f) {
                indexPageInput.setText(null);
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

    public void resize() {
        if (pdfLoader != null && !processing) {
            if (pleineLargeur) {
                scaleSizing = (((float) viewport.getWidth() - (float) scrollPane.getVerticalScrollBar().getWidth()) / (float) pdfLoader.getMinWidth()) - scaleZoom;
            } else {
                scaleSizing = (((float) viewport.getHeight() - (float) scrollPane.getHorizontalScrollBar().getHeight()) / (float) pdfLoader.getMinHeight()) - scaleZoom;
            }
            updateScaleSizing(scaleSizing);
        }
    }

    public void setPleineLargeur(boolean pleineLargeur) {
        this.pleineLargeur = pleineLargeur;
        resize();
    }

    /**
     * Prédicat qui vérifie si un index de page est valide pour le PDF courrant
     *
     * @param index Entier correspondant a l'indice du numéro de page à tester
     * @return true si le prédicat est vérifié, false sinon
     */
    private boolean isPageValide(int index) {
        if (pdfLoader == null) return false;
        return 0 <= index && index < pdfLoader.getNbPages();
    }

    /**
     * Methode qui permet de charger un PDF dans le fenêtre courrante
     *
     * @param pdfFile Le fichier PDF à charger
     * @return true si le PDF à pu se charger, false sinon
     */
    public boolean chargerPdf(File pdfFile) {
        try {
            setPdfLoader(new PdfLoader(pdfFile));
            setPage(0);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @param pdfLoader
     */
    public void setPdfLoader(PdfLoader pdfLoader) {
        this.pdfLoader = pdfLoader;
    }

    /**
     * TODO
     */
    public void dechargerPdf() {
        if (pdfLoader == null) return;

        /* Ferme le loader et l'efface */
        pdfLoader.close();
        pdfLoader = null;

        /* Efface l'image de la page */
        page.setIcon(null);
        currentPage = 0;

        /* Interface Vide */
        indexPageInput.setText("");
        maxPageLabel.setText("/ -");

        validate();
    }

    /**
     * Change la taille de la page courrante
     *
     * @param scale Valeur flottante (1.00f == 100%)
     */
    public void updateScaleZoom(float scale) {
        scaleZoom = scale;
        setPage(currentPage);
    }

    /**
     * Change la taille de la page courrante
     *
     * @param scale Valeur flottante (1.00f == 100%)
     */
    public void updateScaleSizing(float scale) {
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
}
