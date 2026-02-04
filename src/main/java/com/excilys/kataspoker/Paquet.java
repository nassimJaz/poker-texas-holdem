package com.excilys.kataspoker;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Paquet {

    private List<Carte> paquet = new ArrayList<Carte>();

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

    public List<Carte> piocher(int nb) {
        if(nb <= 0 || nb > 3) return null;
        List<Carte> cartesPioches = new ArrayList<Carte>();
        for (int i = 0; i < nb; i++) {
            Carte c = paquet.get(paquet.size() - 1);
            cartesPioches.add(c);
            paquet.remove(c);
        }
        return cartesPioches;
    }

    public List<Carte> piocher() {
        return piocher(1);
    }

    public void afficher() {
        for (int i = 0; i < paquet.size(); i++) {
            System.out.println(paquet.get(i).getValeur() + " de " + paquet.get(i).getCouleur());
        }
    }

    public void sortCouleur() {
        paquet.sort(new ComparateurCouleur());
    }

    public void sortValeur() {
        paquet.sort(new ComparateurValeur());
    }
}
