package com.excilys.kataspoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stratégie IA pour les bots.
 * Prend des décisions basées sur la force de la main, les pot odds, et un
 * soupçon de bluff aléatoire.
 */
public class StrategieBot implements StrategieJoueur {

    private final Random random = new Random();
    private final String nomBot;
    private final boolean delaiActif;

    // Probabilité de bluff (0.0 = jamais, 1.0 = toujours)
    private static final double PROBA_BLUFF = 0.10;

    // Délai simulé pour les actions du bot (ms) — rend le jeu plus naturel
    private static final int DELAI_REFLEXION_MS = 800;

    public StrategieBot(String nomBot) {
        this(nomBot, true);
    }

    public StrategieBot(String nomBot, boolean delaiActif) {
        this.nomBot = nomBot;
        this.delaiActif = delaiActif;
    }

    @Override
    public Actions choisirAction(ContexteDecision ctx, int miseActuelle) {
        simulerReflexion();

        double force = evaluerForce(ctx);
        double potOdds = calculerPotOdds(ctx.getPot(), miseActuelle);

        // Bluff aléatoire : un bot faible peut aussi relancer
        boolean bluff = random.nextDouble() < PROBA_BLUFF;

        Actions action;

        if (miseActuelle == 0) {
            // Personne n'a misé
            if (force > 0.7 || bluff) {
                action = Actions.MISER;
            } else {
                action = Actions.CHECKER;
            }
        } else {
            // Il y a une mise à suivre
            if (force > 0.8 || bluff) {
                action = Actions.RELANCER;
            } else if (force > 0.4 || (force > 0.25 && potOdds > 0.3)) {
                action = Actions.SUIVRE;
            } else {
                action = Actions.PASSER;
            }
        }

        // Affichage de l'action choisie
        System.out.println("  🤖 " + nomBot + " décide de " + formatAction(action));
        return action;
    }

    @Override
    public int choisirMise(ContexteDecision ctx, int miseMinimale) {
        simulerReflexion();

        double force = evaluerForce(ctx);
        int capital = ctx.getCapital();

        // Calcul de la mise proportionnelle à la force
        int mise;
        if (force > 0.85) {
            // Main très forte : grosse relance (50-80% du capital)
            mise = (int) (capital * (0.5 + random.nextDouble() * 0.3));
        } else if (force > 0.65) {
            // Bonne main : mise moyenne (25-50% du capital)
            mise = (int) (capital * (0.25 + random.nextDouble() * 0.25));
        } else {
            // Main correcte ou bluff : petite mise (min + un peu)
            mise = miseMinimale + (int) ((capital - miseMinimale) * 0.1 * random.nextDouble()) + 1;
        }

        // S'assurer que la mise est valide
        mise = Math.max(mise, miseMinimale + 1);
        mise = Math.min(mise, capital);

        System.out.println("  🤖 " + nomBot + " mise " + mise + " jetons");
        return mise;
    }

    // ========================== ÉVALUATION DE LA FORCE ==========================

    /**
     * Évalue la force globale de la main (entre 0.0 et 1.0).
     */
    private double evaluerForce(ContexteDecision ctx) {
        if (ctx.isPreFlop()) {
            return evaluerForcePreFlop(ctx.getHand());
        } else {
            return evaluerForcePostFlop(ctx);
        }
    }

    /**
     * Évalue la force de la main de départ (pré-flop).
     * Basée sur une classification simplifiée des mains de départ.
     * Retourne un score entre 0.0 et 1.0.
     */
    private double evaluerForcePreFlop(List<Carte> hand) {
        if (hand.size() < 2)
            return 0.3;

        int r1 = hand.get(0).getValeur().getRang();
        int r2 = hand.get(1).getValeur().getRang();
        boolean paire = (r1 == r2);
        boolean suited = hand.get(0).getCouleur() == hand.get(1).getCouleur();
        int haut = Math.max(r1, r2);
        int bas = Math.min(r1, r2);
        int ecart = haut - bas;

        // Paires — toujours intéressantes
        if (paire) {
            if (haut >= 12)
                return 0.95; // QQ, KK, AA
            if (haut >= 9)
                return 0.80; // 99, TT, JJ
            if (haut >= 6)
                return 0.60; // 66, 77, 88
            return 0.45; // 22-55
        }

        // Cartes hautes
        double score = 0.0;

        // Valeur brute des cartes (normalisée)
        score += (haut - 2.0) / 12.0 * 0.4; // max 0.4 pour un As
        score += (bas - 2.0) / 12.0 * 0.2; // max 0.2 pour un As

        // Bonus suited
        if (suited)
            score += 0.08;

        // Bonus connecteurs (cartes proches pour quinte)
        if (ecart <= 1)
            score += 0.10;
        else if (ecart <= 2)
            score += 0.05;
        else if (ecart <= 3)
            score += 0.02;

        // Penalty pour gros écart
        if (ecart >= 5)
            score -= 0.05;

        // Cas spéciaux connus
        if (haut == 14 && bas == 13)
            score = Math.max(score, 0.85); // AK
        if (haut == 14 && bas == 12)
            score = Math.max(score, 0.75); // AQ
        if (haut == 14 && bas == 11)
            score = Math.max(score, 0.65); // AJ
        if (haut == 13 && bas == 12)
            score = Math.max(score, 0.70); // KQ

        return Math.max(0.05, Math.min(1.0, score));
    }

    /**
     * Évalue la force post-flop en utilisant EvaluateurMain.
     * Normalise le résultat entre 0.0 et 1.0.
     */
    private double evaluerForcePostFlop(ContexteDecision ctx) {
        List<Carte> toutesCartes = ctx.getToutesCartes();

        // S'il n'y a pas assez de cartes pour évaluer, fallback pré-flop
        if (toutesCartes.size() < 5) {
            return evaluerForcePreFlop(ctx.getHand());
        }

        // Évaluer avec les cartes disponibles (peut être 5, 6 ou 7 cartes)
        ResultatMain resultat;
        if (toutesCartes.size() >= 7) {
            resultat = EvaluateurMain.evaluer(toutesCartes);
        } else {
            // Moins de 7 cartes : évaluer directement les 5 ou 6 disponibles
            resultat = evaluerAvecCartesDisponibles(toutesCartes);
        }

        Combinaison combo = resultat.getCombinaison();

        // Normaliser la combinaison en score
        switch (combo) {
            case QUINTE_FLUSH_ROYALE:
                return 1.00;
            case QUINTE_FLUSH:
                return 0.97;
            case CARRE:
                return 0.94;
            case FULL:
                return 0.90;
            case COULEUR_FLUSH:
                return 0.85;
            case QUINTE:
                return 0.80;
            case BRELAN:
                return 0.70;
            case DOUBLE_PAIRE:
                return 0.55;
            case PAIRE: {
                // Score ajusté selon la hauteur de la paire
                int rangPaire = resultat.getValeursCles().get(0);
                return 0.30 + (rangPaire - 2.0) / 12.0 * 0.15;
            }
            case HAUTE_CARTE: {
                int rangHaut = resultat.getValeursCles().get(0);
                return 0.10 + (rangHaut - 2.0) / 12.0 * 0.15;
            }
            default:
                return 0.20;
        }
    }

    /**
     * Évalue une main avec moins de 7 cartes (flop = 5, turn = 6).
     * Pour 5 cartes, évalue directement. Pour 6, teste les C(6,5) = 6 combos.
     */
    private ResultatMain evaluerAvecCartesDisponibles(List<Carte> cartes) {
        if (cartes.size() == 5) {
            // Passer par evaluer avec exactement 5 — on crée un wrapper
            // On ajoute des cartes fictives puis on prend la meilleure parmi les 5
            return EvaluateurMain.evaluer(cartes);
        }
        // Pour 6 cartes, tester les 6 combinaisons de 5
        ResultatMain meilleur = null;
        for (int i = 0; i < cartes.size(); i++) {
            List<Carte> sous = new ArrayList<>(cartes);
            sous.remove(i);
            // Utiliser evaluer qui gère les listes de taille >= 5
            ResultatMain r = EvaluateurMain.evaluer(sous);
            if (meilleur == null || r.compareTo(meilleur) > 0) {
                meilleur = r;
            }
        }
        return meilleur;
    }

    // ========================== UTILITAIRES ==========================

    /**
     * Calcule les pot odds : ratio entre la mise à payer et le pot total.
     * Plus le ratio est élevé, plus c'est "rentable" de suivre.
     */
    private double calculerPotOdds(int pot, int miseActuelle) {
        if (miseActuelle == 0)
            return 1.0;
        return (double) pot / (pot + miseActuelle);
    }

    /**
     * Simule un temps de réflexion pour rendre le jeu plus naturel.
     */
    private void simulerReflexion() {
        if (!delaiActif)
            return;
        try {
            int delai = DELAI_REFLEXION_MS + random.nextInt(500);
            Thread.sleep(delai);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String formatAction(Actions action) {
        switch (action) {
            case CHECKER:
                return "CHECKER ✓";
            case MISER:
                return "MISER 💰";
            case SUIVRE:
                return "SUIVRE →";
            case RELANCER:
                return "RELANCER ⬆";
            case PASSER:
                return "PASSER ✗";
            default:
                return action.name();
        }
    }
}
