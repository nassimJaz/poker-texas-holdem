package com.excilys.kataspoker.strategy;

import com.excilys.kataspoker.model.Carte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contexte transmis aux stratégies de jeu pour prendre une décision.
 * Contient toutes les informations visibles par le joueur.
 */
public class ContexteDecision {

    private final List<Carte> hand;
    private final List<Carte> board;
    private final int capital;
    private final int pot;
    private final int miseActuelle;
    private final int nbJoueursActifs;

    public ContexteDecision(List<Carte> hand, List<Carte> board, int capital,
            int pot, int miseActuelle, int nbJoueursActifs) {
        this.hand = Collections.unmodifiableList(new ArrayList<>(hand));
        this.board = Collections.unmodifiableList(new ArrayList<>(board));
        this.capital = capital;
        this.pot = pot;
        this.miseActuelle = miseActuelle;
        this.nbJoueursActifs = nbJoueursActifs;
    }

    public List<Carte> getHand() {
        return hand;
    }

    public List<Carte> getBoard() {
        return board;
    }

    public int getCapital() {
        return capital;
    }

    public int getPot() {
        return pot;
    }

    public int getMiseActuelle() {
        return miseActuelle;
    }

    public int getNbJoueursActifs() {
        return nbJoueursActifs;
    }

    public List<Carte> getToutesCartes() {
        List<Carte> toutes = new ArrayList<>(hand);
        toutes.addAll(board);
        return toutes;
    }

    public boolean isPreFlop() {
        return board.isEmpty();
    }
}
