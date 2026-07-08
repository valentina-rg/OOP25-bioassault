package it.unibo.bioassault.view;

/**
 * Rappresenta un'opzione di potenziamento mostrata al level-up.
 * La schermata LevelUpScreen (in GameScreens) mostra 3 di queste card
 * e il giocatore sceglie quale upgrade applicare.
 */
public class UpgradeOption {

    public final String name;        // nome dell'upgrade (es. "Velocita' aumentata")
    public final String description; // descrizione breve mostrata nella card
    public final String icon;        // simbolo/emoji testuale (es. "⚡", "❤", "★")

    public UpgradeOption(final String name,
                         final String description,
                         final String icon) {
        this.name        = name;
        this.description = description;
        this.icon        = icon;
    }
}
