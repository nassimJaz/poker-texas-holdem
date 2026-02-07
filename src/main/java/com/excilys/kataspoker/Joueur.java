package com.excilys.kataspoker;

public class Joueur {

    private String pseudo = "";
    private double score;
    private final Hand hand;
    private int capital;
    private int mise;

    private Actions action;
    

    public Joueur(String pseudo) {
        this.pseudo = pseudo;
        this.hand = new Hand();

        this.capital = 5000;
        this.mise = 0;
        this.score = 0.0;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Hand getHand() {
        return hand;
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public int getMise() {
        return mise;
    }

    public void setMise(int mise) {
        if(mise < 0) {
            System.out.println("Erreur : impossible de miser une somme inférieur à 0$");
        }
        this.mise = mise;
    }

    public Actions getAction() {
        return action;
    }

    public void setAction(Actions action) {
        this.action = action;
    }
}
