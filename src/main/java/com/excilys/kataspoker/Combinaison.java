package com.excilys.kataspoker;

public enum Combinaison {
    HAUTE_CARTE(0, "Haute carte"),
    PAIRE(1, "Paire"),
    DOUBLE_PAIRE(2, "Double paire"),
    BRELAN(3, "Brelan"),
    QUINTE(4, "Quinte"),
    COULEUR_FLUSH(5, "Couleur"),
    FULL(6, "Full"),
    CARRE(7, "Carré"),
    QUINTE_FLUSH(8, "Quinte flush"),
    QUINTE_FLUSH_ROYALE(9, "Quinte flush royale");

    private final int force;
    private final String nom;

    Combinaison(int force, String nom) {
        this.force = force;
        this.nom = nom;
    }

    public int getForce() {
        return force;
    }

    public String getNom() {
        return nom;
    }
}
