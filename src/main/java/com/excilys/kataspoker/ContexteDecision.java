package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contexte transmis aux stratégies de jeu pour prendre une décision.
 * Contient toutes les informations visibles par le joueur.
 */
public class ContexteDecision {

    private final List<Carte> hand; // Cartes du joueur (2 cartes)
    private final List<Carte> board; // Cartes communautaires
    private final int capital; // Capital du joueur
    private final int pot; // Pot total
    private final int miseActuelle; // Mise en cours à suivre
    private final int nbJoueursActifs; // Nombre de joueurs encore en jeu

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

    /**
     * Retourne toutes les cartes disponibles (main + board).
     */
    public List<Carte> getToutesCartes() {
        List<Carte> toutes = new ArrayList<>(hand);
        toutes.addAll(board);
        return toutes;
    }

    /**
     * Indique si on est en phase pré-flop (board vide).
     */
    public boolean isPreFlop() {
        return board.isEmpty();
    }
}
