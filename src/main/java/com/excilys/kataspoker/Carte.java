package com.excilys.kataspoker;

public class Carte {
    private Valeurs valeur;
    private Couleur couleur;

    public Carte(Valeurs valeur, Couleur couleur) {
        this.valeur = valeur;
        this.couleur = couleur;
    }

    public Carte(Carte c) {
        this.valeur = c.valeur;
        this.couleur = c.couleur;
    }

    public Valeurs getValeur() {
        return valeur;
    }

    public void setValeur(Valeurs valeur) {
        this.valeur = valeur;
    }

    public Couleur getFamille() {
        return couleur;
    }

    public void setFamille(Couleur couleur) {
        this.couleur = couleur;
    }
}
