package com.excilys.kataspoker;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Paquet {

    private final List<Carte> paquet = new ArrayList<Carte>();

    public Paquet() {
        init();
    }

    private void init() {
        for(Couleur couleur : Couleur.values()) {
            for(Valeurs valeurs : Valeurs.values()) {
                Carte carte = new Carte(valeurs, couleur);
                //System.out.println(carte.getFamille() + " - " + carte.getValeur());
                paquet.add(carte);
            }
        }
    }

    public List<Carte> getPaquet() {
        return paquet;
    }

    public void shuffle() {
        Collections.shuffle(paquet);
    }
}
