/*
 * ZoomMoins.java, 26/02/2022
 * IUT Rodez 2021-2022, INFO 2
 * pas de copyright, aucun droits
 */

package lecteur_pdf.menuBar.menuItems;

import lecteur_pdf.IhmPdf;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * TODO commentaires
 *
 * @author Léo Franch
 * @author Lucas Vabre
 * @author Noé Villeneuve
 * @author Tristan Nogaret
 */
public class ZoomMoins extends MenuItem {

    /**
     * TODO
     * @param parent
     */
    public ZoomMoins(IhmPdf parent) {
        super(parent, "Zoom 50%");

//        setRaccourcis(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);

    }

    @Override
    protected void action(ActionEvent evt) {
        parent.getPdfPanel().updateScaleZoom(0.5f);
        parent.validate();
    }
}
