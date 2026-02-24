package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Table {

    private List<Joueur> joueurs = new ArrayList<Joueur>();
    private int nbJoueurs = 0;
    private Paquet paquet = new Paquet();

    private List<Carte> board = new ArrayList<Carte>();
    private int pot;
    private int indexDealer;
    private static final int PRIX_BLINDE = 25;
    private static final int PRIX_GROSSE_BLINDE = PRIX_BLINDE * 2;

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String ROUGE = "\u001B[31m";
    private static final String VERT = "\u001B[32m";
    private static final String JAUNE = "\u001B[33m";
    private static final String BLEU = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRIS = "\u001B[90m";
    private static final String GRAS = "\u001B[1m";

    public Table(List<Joueur> joueurs) {
        this.joueurs = joueurs;
        this.nbJoueurs = this.joueurs.size();
        this.indexDealer = 0;
        this.pot = 0;
    }

    // ========================== UTILITAIRES ==========================

    public Joueur getJoueur(int indexJoueur) {
        int index = indexJoueur % nbJoueurs;
        return joueurs.get(index);
    }

    public Joueur getJoueurSuivant(int indexJoueur) {
        int indexSuivant = (indexJoueur + 1) % nbJoueurs;
        return joueurs.get(indexSuivant);
    }

    public Joueur getJoueurSuivant(int indexJoueur, int decalage) {
        int index = (indexJoueur + decalage) % nbJoueurs;
        return joueurs.get(index);
    }

    /**
     * Retourne les joueurs encore en jeu dans cette manche (non couchés, non
     * éliminés).
     */
    private List<Joueur> getJoueursActifs() {
        return joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                .collect(Collectors.toList());
    }

    /**
     * Retourne le symbole Unicode coloré pour une couleur de carte.
     */
    private String symboleCouleur(Couleur couleur) {
        switch (couleur) {
            case COEUR:
                return ROUGE + "♥" + RESET;
            case CARREAUX:
                return ROUGE + "♦" + RESET;
            case PIQUE:
                return "♠";
            case TREFLE:
                return "♣";
            default:
                return couleur.name();
        }
    }

    /**
     * Formate une carte avec symbole Unicode.
     */
    private String formatCarte(Carte carte) {
        String valeur;
        switch (carte.getValeur()) {
            case AS:
                valeur = "A";
                break;
            case ROI:
                valeur = "K";
                break;
            case DAME:
                valeur = "Q";
                break;
            case VALET:
                valeur = "J";
                break;
            case DIX:
                valeur = "10";
                break;
            default:
                valeur = String.valueOf(carte.getValeur().getRang());
                break;
        }
        return valeur + symboleCouleur(carte.getCouleur());
    }

    /**
     * Construit le contexte de décision pour un joueur.
     */
    private ContexteDecision construireContexte(Joueur joueur, int miseActuelle) {
        int nbActifs = (int) joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                .count();
        return new ContexteDecision(
                joueur.getHand().getHand(),
                board,
                joueur.getCapital(),
                pot,
                miseActuelle,
                nbActifs);
    }

    // ========================== DISTRIBUTION ==========================

    public void nbCartesBoard(int nbCartes) {
        for (int i = 0; i < nbCartes; i++) {
            Carte c = paquet.piocher();
            board.add(c);
        }
    }

    public void nbCartesParJoueur(int nbCartes) {
        for (int i = 0; i < nbCartes * this.nbJoueurs; i++) {
            Carte carte = paquet.piocher();
            this.getJoueur(i).getHand().addHand(carte);
        }
    }

    // ========================== MISES ==========================

    public void transfertMise(int mise, Joueur j) {
        int montant = j.miser(mise);
        pot += montant;
    }

    public void initialiserMises() {
        Joueur joueurPetiteBlinde = getJoueurSuivant(indexDealer);
        Joueur joueurGrosseBlinde = getJoueurSuivant(indexDealer, 2);

        System.out.println("\n" + JAUNE + "═══════════ BLINDES ═══════════" + RESET);
        System.out.println("  " + joueurPetiteBlinde.getPseudo() + " → petite blinde : " + PRIX_BLINDE);
        this.transfertMise(PRIX_BLINDE, joueurPetiteBlinde);
        System.out.println("  " + joueurGrosseBlinde.getPseudo() + " → grosse blinde : " + PRIX_GROSSE_BLINDE);
        this.transfertMise(PRIX_GROSSE_BLINDE, joueurGrosseBlinde);
        System.out.println(JAUNE + "═══════════════════════════════" + RESET + "\n");
    }

    public void initialiserManche() {
        paquet.reset();
        paquet.shuffle();
        board.clear();
        pot = 0;
        for (Joueur j : joueurs) {
            j.clearHand();
            j.setAction(null);
            j.resetEtatAllIn();
        }
        nbCartesParJoueur(2); // Distribue 2 cartes par joueur
        initialiserMises();
    }

    // ========================== MANCHE COMPLÈTE ==========================

    /**
     * Joue une manche complète de Texas Hold'em.
     * Retourne true si la partie peut continuer, false sinon.
     */
    public boolean mancheTable() {
        System.out.println("\n" + GRAS + CYAN + "╔══════════════════════════════════════╗" + RESET);
        System.out.println(GRAS + CYAN + "║       NOUVELLE MANCHE                ║" + RESET);
        System.out.println(GRAS + CYAN + "║  Dealer : " + getJoueur(indexDealer).getPseudo() + RESET);
        System.out.println(GRAS + CYAN + "╚══════════════════════════════════════╝" + RESET);

        initialiserManche();

        // Pré-flop
        afficherBoard();
        int indexPremierParole = (indexDealer + 3) % nbJoueurs;
        if (tourDeMise(indexPremierParole, PRIX_GROSSE_BLINDE)) {
            finMancheFold();
            return true;
        }

        // Flop
        nbCartesBoard(3);
        afficherBoard();
        indexPremierParole = (indexDealer + 1) % nbJoueurs;
        if (tourDeMise(indexPremierParole, 0)) {
            finMancheFold();
            return true;
        }

        // Turn
        nbCartesBoard(1);
        afficherBoard();
        if (tourDeMise(indexPremierParole, 0)) {
            finMancheFold();
            return true;
        }

        // River
        nbCartesBoard(1);
        afficherBoard();
        if (tourDeMise(indexPremierParole, 0)) {
            finMancheFold();
            return true;
        }

        // Showdown
        showdown();

        // Avancer le dealer
        indexDealer = (indexDealer + 1) % nbJoueurs;
        return true;
    }

    /**
     * Quand tous les joueurs sauf un se sont couchés.
     */
    private void finMancheFold() {
        List<Joueur> actifs = getJoueursActifs();
        if (actifs.size() == 1) {
            Joueur gagnant = actifs.get(0);
            gagnant.gagnerPot(pot);
            System.out.println("\n" + VERT + GRAS + "🏆 " + gagnant.getPseudo()
                    + " remporte " + pot + " jetons (les autres se sont couchés)" + RESET);
        }
        afficherCapitaux();
        indexDealer = (indexDealer + 1) % nbJoueurs;
    }

    // ========================== SHOWDOWN ==========================

    private void showdown() {
        System.out.println("\n" + GRAS + JAUNE + "══════════ SHOWDOWN ══════════" + RESET);

        List<Joueur> joueursActifs = getJoueursActifs();
        Map<Joueur, ResultatMain> resultats = new HashMap<>();

        for (Joueur j : joueursActifs) {
            List<Carte> cartes7 = new ArrayList<>();
            cartes7.addAll(j.getHand().getHand());
            cartes7.addAll(board);

            ResultatMain resultat = EvaluateurMain.evaluer(cartes7);
            resultats.put(j, resultat);

            System.out.println("  " + j.getPseudo() + " : ");
            System.out.print("    Cartes : ");
            for (Carte c : j.getHand().getHand()) {
                System.out.print(formatCarte(c) + " ");
            }
            System.out.println();
            System.out.println("    → " + GRAS + resultat.getCombinaison().getNom() + RESET);
        }

        // Trouver le meilleur résultat
        ResultatMain meilleur = null;
        for (ResultatMain r : resultats.values()) {
            if (meilleur == null || r.compareTo(meilleur) > 0) {
                meilleur = r;
            }
        }

        // Trouver tous les gagnants (en cas d'égalité parfaite)
        final ResultatMain meilleurFinal = meilleur;
        List<Joueur> gagnants = new ArrayList<>();
        for (Map.Entry<Joueur, ResultatMain> entry : resultats.entrySet()) {
            if (entry.getValue().compareTo(meilleurFinal) == 0) {
                gagnants.add(entry.getKey());
            }
        }

        // Distribuer le pot
        System.out.println();
        if (gagnants.size() == 1) {
            Joueur gagnant = gagnants.get(0);
            gagnant.gagnerPot(pot);
            System.out.println(VERT + GRAS + "🏆 " + gagnant.getPseudo() + " remporte "
                    + pot + " jetons avec " + meilleur.getCombinaison().getNom() + " !" + RESET);
        } else {
            int partPot = pot / gagnants.size();
            System.out.println(VERT + GRAS + "🤝 Égalité ! Split du pot :" + RESET);
            for (Joueur g : gagnants) {
                g.gagnerPot(partPot);
                System.out.println("  " + g.getPseudo() + " reçoit " + partPot + " jetons");
            }
        }

        System.out.println(JAUNE + "══════════════════════════════" + RESET);
        afficherCapitaux();
    }

    // ========================== TOUR DE MISE ==========================

    public void resetActionsJoueurs(boolean resetHard) {
        for (int i = 0; i < nbJoueurs; i++) {
            Joueur j = getJoueur(i);
            j.setAction(null);
            if (resetHard)
                j.resetEtatAllIn();
        }
    }

    /**
     * Demande une action à un joueur en déléguant à sa stratégie.
     * Affiche le contexte différemment selon que c'est un humain ou un bot.
     */
    public void demanderAction(Joueur joueur, int miseActuelle) {
        System.out.println("\n" + BLEU + "━━━ Tour de " + GRAS + joueur.getPseudo() + RESET
                + (joueur.isBot() ? " 🤖" : "") + BLEU + " ━━━" + RESET);
        System.out.println("  Capital : " + joueur.getCapital() + " jetons");

        // N'afficher les cartes que pour les humains (les bots cachent leur jeu)
        if (!joueur.isBot()) {
            System.out.print("  Cartes : ");
            for (Carte c : joueur.getHand().getHand()) {
                System.out.print(formatCarte(c) + " ");
            }
            System.out.println();
        } else {
            System.out.println("  Cartes : " + GRIS + "[cachées]" + RESET);
        }

        ContexteDecision ctx = construireContexte(joueur, miseActuelle);
        Actions action = joueur.getStrategie().choisirAction(ctx, miseActuelle);
        joueur.setAction(action);
    }

    /**
     * Demande le montant de la mise en déléguant à la stratégie du joueur.
     */
    public int demanderMise(Joueur joueur, int miseEnCours) {
        ContexteDecision ctx = construireContexte(joueur, miseEnCours);
        return joueur.getStrategie().choisirMise(ctx, miseEnCours);
    }

    public boolean tourDeMise(int indexPremierJoueur, int miseActuelle) {

        Map<Joueur, Integer> misesTour = new HashMap<>();
        for (Joueur j : joueurs) {
            misesTour.put(j, 0);
        }

        int joueursActifs = (int) joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER && !j.getEtatAllIn())
                .count();

        int joueursARepondre = joueursActifs;
        int indexCourant = indexPremierJoueur;

        while (joueursARepondre > 0) {

            Joueur joueur = getJoueur(indexCourant);

            // Passer les joueurs éliminés, couchés ou all-in
            if (joueur.isElimine() || joueur.getEtatAllIn() || joueur.getAction() == Actions.PASSER) {
                indexCourant++;
                continue;
            }

            demanderAction(joueur, miseActuelle);
            Actions action = joueur.getAction();

            switch (action) {
                case CHECKER:
                    joueursARepondre--;
                    break;

                case MISER:
                    miseActuelle = demanderMise(joueur, miseActuelle);
                    transfertMise(miseActuelle, joueur);
                    misesTour.put(joueur, miseActuelle);
                    joueursARepondre = joueursActifs - 1;
                    break;

                case SUIVRE:
                    int dejaMis = misesTour.get(joueur);
                    int aPayer = miseActuelle - dejaMis;
                    if (aPayer > 0) {
                        transfertMise(aPayer, joueur);
                        misesTour.put(joueur, miseActuelle);
                    }
                    joueursARepondre--;
                    break;

                case RELANCER:
                    int nouvelleMise = demanderMise(joueur, miseActuelle);
                    int dejaMisRelance = misesTour.get(joueur);
                    int aPayerRelance = nouvelleMise - dejaMisRelance;
                    transfertMise(aPayerRelance, joueur);
                    miseActuelle = nouvelleMise;
                    misesTour.put(joueur, miseActuelle);
                    joueursARepondre = joueursActifs - 1;
                    break;

                case PASSER:
                    joueursActifs--;
                    joueursARepondre--;
                    break;
            }

            // Vérifier s'il ne reste qu'un seul joueur actif
            long restants = joueurs.stream()
                    .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                    .count();
            if (restants <= 1) {
                resetActionsJoueurs(false);
                return true; // Manche terminée prématurément
            }

            indexCourant++;
        }

        resetActionsJoueurs(false);
        return false;
    }

    // ========================== AFFICHAGE ==========================

    public void afficherBoard() {
        System.out.println("\n" + CYAN + "┌──────────────────────────────┐" + RESET);
        if (board.isEmpty()) {
            System.out.println(CYAN + "│" + RESET + "  Board : (pas de cartes)     " + CYAN + "│" + RESET);
        } else {
            System.out.print(CYAN + "│" + RESET + "  Board : ");
            for (int i = 0; i < board.size(); i++) {
                System.out.print(formatCarte(board.get(i)));
                if (i < board.size() - 1)
                    System.out.print(" | ");
            }
            System.out.println();
        }
        System.out.println(CYAN + "│" + RESET + "  Pot : " + JAUNE + GRAS + pot + RESET + " jetons");
        System.out.println(CYAN + "└──────────────────────────────┘" + RESET);
    }

    public void afficherCapitaux() {
        System.out.println("\n" + GRAS + "📊 Capitaux :" + RESET);
        for (Joueur j : joueurs) {
            String statut = j.isElimine() ? " (éliminé)" : "";
            String icone = j.isBot() ? " 🤖" : "";
            String couleur = j.isElimine() ? GRIS : "";
            System.out.println(
                    couleur + "  " + j.getPseudo() + icone + " : " + j.getCapital() + " jetons" + statut + RESET);
        }
    }

    // ========================== GESTION PARTIE ==========================

    /**
     * Élimine les joueurs à 0 de capital.
     */
    public void eliminerJoueurs() {
        for (Joueur j : joueurs) {
            if (j.getCapital() <= 0 && !j.isElimine()) {
                j.setElimine(true);
                System.out.println("\n" + ROUGE + "💀 " + j.getPseudo() + " est éliminé !" + RESET);
            }
        }
    }

    /**
     * Retourne le nombre de joueurs encore en jeu (avec du capital).
     */
    public int nbJoueursEnVie() {
        return (int) joueurs.stream().filter(j -> !j.isElimine()).count();
    }

    /**
     * Retourne le dernier joueur en vie (gagnant de la partie).
     */
    public Joueur getGagnantPartie() {
        return joueurs.stream().filter(j -> !j.isElimine()).findFirst().orElse(null);
    }
}
