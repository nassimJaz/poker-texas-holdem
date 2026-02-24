package com.excilys.kataspoker.game;

import com.excilys.kataspoker.model.*;
import com.excilys.kataspoker.strategy.*;

public class Joueur {

    private String pseudo = "";
    private double score;
    private final Hand hand;
    private int capital;

    private Actions action;
    private Boolean allIn;
    private boolean elimine;

    private StrategieJoueur strategie;

    public Joueur(String pseudo) {
        this(pseudo, null);
    }

    public Joueur(String pseudo, StrategieJoueur strategie) {
        this.pseudo = pseudo;
        this.hand = new Hand();
        this.capital = 5000;
        this.score = 0.0;
        this.allIn = false;
        this.elimine = false;
        this.strategie = strategie;
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

    public Actions getAction() {
        return action;
    }

    public void setAction(Actions action) {
        this.action = action;
    }

    public void resetEtatAllIn() {
        this.allIn = false;
    }

    public boolean getEtatAllIn() {
        return this.allIn;
    }

    public boolean isElimine() {
        return this.elimine;
    }

    public void setElimine(boolean elimine) {
        this.elimine = elimine;
    }

    public StrategieJoueur getStrategie() {
        return strategie;
    }

    public void setStrategie(StrategieJoueur strategie) {
        this.strategie = strategie;
    }

    public boolean isBot() {
        return strategie instanceof StrategieBot;
    }

    public int miser(int montantMise) {
        if (montantMise > this.capital) {
            this.allIn = true;
            montantMise = this.capital;
            this.capital -= montantMise;
        } else {
            this.capital -= montantMise;
        }
        return montantMise;
    }

    public void gagnerPot(int montant) {
        this.capital += montant;
    }

    public void clearHand() {
        this.hand.clear();
    }
}
