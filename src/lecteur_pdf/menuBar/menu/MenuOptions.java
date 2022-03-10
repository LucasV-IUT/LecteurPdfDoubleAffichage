/*
 * MenuOptions.java, 26/02/2022
 * IUT Rodez 2021-2022, INFO 2
 * pas de copyright, aucun droits
 */

package lecteur_pdf.menuBar.menu;

import lecteur_pdf.Fenetre;
import lecteur_pdf.menuBar.menuItems.ModifierTouches;

import javax.swing.*;

/**
 * Element de la barre des menus contenant l'intégralité des options de
 * l'application
 *
 * @author Léo Franch
 * @author Lucas Vabre
 * @author Noé Villeneuve
 * @author Tristan Nogaret
 */
public class MenuOptions extends JMenu {

    /**
     * Crée une nouvelle instance du menu Options
     *
     * @param parent Référence de la fenêtre qui possède l'instance de ce menu
     */
    public MenuOptions(Fenetre parent) {
        super("Options");
        add(new ModifierTouches(parent));
    }
}
