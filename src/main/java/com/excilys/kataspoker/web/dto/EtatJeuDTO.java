package com.excilys.kataspoker.web.dto;

import java.util.List;

/**
 * État complet du jeu envoyé au frontend.
 */
public class EtatJeuDTO {

    private String partieId;
    private String phase; // LOBBY, PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN, FIN_MANCHE, FIN_PARTIE,
                          // PASSAGE_JOUEUR
    private int manche;
    private int pot;
    private int miseActuelle;
    private List<CarteDTO> board;
    private List<JoueurDTO> joueurs;
    private int joueurActifIndex;
    private boolean actionRequise;
    private List<String> actionsDisponibles;
    private String message;
    private String mode; // SOLO_BOTS, MULTIJOUEUR
    private List<String> logActions;

    // ---- Inner DTOs ----

    public static class CarteDTO {
        private String valeur;
        private String couleur;
        private String symbole; // ♠ ♥ ♦ ♣

        public CarteDTO() {
        }

        public CarteDTO(String valeur, String couleur, String symbole) {
            this.valeur = valeur;
            this.couleur = couleur;
            this.symbole = symbole;
        }

        public String getValeur() {
            return valeur;
        }

        public void setValeur(String valeur) {
            this.valeur = valeur;
        }

        public String getCouleur() {
            return couleur;
        }

        public void setCouleur(String couleur) {
            this.couleur = couleur;
        }

        public String getSymbole() {
            return symbole;
        }

        public void setSymbole(String symbole) {
            this.symbole = symbole;
        }
    }

    public static class JoueurDTO {
        private String pseudo;
        private int capital;
        private boolean isBot;
        private boolean elimine;
        private boolean actif; // pas couché
        private String action; // dernière action
        private List<CarteDTO> cartes; // null si cachées
        private String combinaison; // rempli au showdown

        public String getPseudo() {
            return pseudo;
        }

        public void setPseudo(String pseudo) {
            this.pseudo = pseudo;
        }

        public int getCapital() {
            return capital;
        }

        public void setCapital(int capital) {
            this.capital = capital;
        }

        public boolean isBot() {
            return isBot;
        }

        public void setBot(boolean bot) {
            isBot = bot;
        }

        public boolean isElimine() {
            return elimine;
        }

        public void setElimine(boolean elimine) {
            this.elimine = elimine;
        }

        public boolean isActif() {
            return actif;
        }

        public void setActif(boolean actif) {
            this.actif = actif;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public List<CarteDTO> getCartes() {
            return cartes;
        }

        public void setCartes(List<CarteDTO> cartes) {
            this.cartes = cartes;
        }

        public String getCombinaison() {
            return combinaison;
        }

        public void setCombinaison(String combinaison) {
            this.combinaison = combinaison;
        }
    }

    // ---- Getters / Setters ----

    public String getPartieId() {
        return partieId;
    }

    public void setPartieId(String partieId) {
        this.partieId = partieId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public int getManche() {
        return manche;
    }

    public void setManche(int manche) {
        this.manche = manche;
    }

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }

    public int getMiseActuelle() {
        return miseActuelle;
    }

    public void setMiseActuelle(int miseActuelle) {
        this.miseActuelle = miseActuelle;
    }

    public List<CarteDTO> getBoard() {
        return board;
    }

    public void setBoard(List<CarteDTO> board) {
        this.board = board;
    }

    public List<JoueurDTO> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<JoueurDTO> joueurs) {
        this.joueurs = joueurs;
    }

    public int getJoueurActifIndex() {
        return joueurActifIndex;
    }

    public void setJoueurActifIndex(int joueurActifIndex) {
        this.joueurActifIndex = joueurActifIndex;
    }

    public boolean isActionRequise() {
        return actionRequise;
    }

    public void setActionRequise(boolean actionRequise) {
        this.actionRequise = actionRequise;
    }

    public List<String> getActionsDisponibles() {
        return actionsDisponibles;
    }

    public void setActionsDisponibles(List<String> actionsDisponibles) {
        this.actionsDisponibles = actionsDisponibles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getLogActions() {
        return logActions;
    }

    public void setLogActions(List<String> logActions) {
        this.logActions = logActions;
    }
}
