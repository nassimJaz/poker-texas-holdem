package com.excilys.kataspoker.strategy;

import com.excilys.kataspoker.evaluation.EvaluateurMain;
import com.excilys.kataspoker.model.*;

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

    private static final double PROBA_BLUFF = 0.10;
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
        boolean bluff = random.nextDouble() < PROBA_BLUFF;

        Actions action;

        if (miseActuelle == 0) {
            if (force > 0.7 || bluff) {
                action = Actions.MISER;
            } else {
                action = Actions.CHECKER;
            }
        } else {
            if (force > 0.8 || bluff) {
                action = Actions.RELANCER;
            } else if (force > 0.4 || (force > 0.25 && potOdds > 0.3)) {
                action = Actions.SUIVRE;
            } else {
                action = Actions.PASSER;
            }
        }

        System.out.println("  🤖 " + nomBot + " décide de " + formatAction(action));
        return action;
    }

    @Override
    public int choisirMise(ContexteDecision ctx, int miseMinimale) {
        simulerReflexion();

        double force = evaluerForce(ctx);
        int capital = ctx.getCapital();

        int mise;
        if (force > 0.85) {
            mise = (int) (capital * (0.5 + random.nextDouble() * 0.3));
        } else if (force > 0.65) {
            mise = (int) (capital * (0.25 + random.nextDouble() * 0.25));
        } else {
            mise = miseMinimale + (int) ((capital - miseMinimale) * 0.1 * random.nextDouble()) + 1;
        }

        mise = Math.max(mise, miseMinimale + 1);
        mise = Math.min(mise, capital);

        System.out.println("  🤖 " + nomBot + " mise " + mise + " jetons");
        return mise;
    }

    // ========================== ÉVALUATION DE LA FORCE ==========================

    private double evaluerForce(ContexteDecision ctx) {
        if (ctx.isPreFlop()) {
            return evaluerForcePreFlop(ctx.getHand());
        } else {
            return evaluerForcePostFlop(ctx);
        }
    }

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

        if (paire) {
            if (haut >= 12)
                return 0.95;
            if (haut >= 9)
                return 0.80;
            if (haut >= 6)
                return 0.60;
            return 0.45;
        }

        double score = 0.0;
        score += (haut - 2.0) / 12.0 * 0.4;
        score += (bas - 2.0) / 12.0 * 0.2;

        if (suited)
            score += 0.08;

        if (ecart <= 1)
            score += 0.10;
        else if (ecart <= 2)
            score += 0.05;
        else if (ecart <= 3)
            score += 0.02;

        if (ecart >= 5)
            score -= 0.05;

        if (haut == 14 && bas == 13)
            score = Math.max(score, 0.85);
        if (haut == 14 && bas == 12)
            score = Math.max(score, 0.75);
        if (haut == 14 && bas == 11)
            score = Math.max(score, 0.65);
        if (haut == 13 && bas == 12)
            score = Math.max(score, 0.70);

        return Math.max(0.05, Math.min(1.0, score));
    }

    private double evaluerForcePostFlop(ContexteDecision ctx) {
        List<Carte> toutesCartes = ctx.getToutesCartes();

        if (toutesCartes.size() < 5) {
            return evaluerForcePreFlop(ctx.getHand());
        }

        ResultatMain resultat;
        if (toutesCartes.size() >= 7) {
            resultat = EvaluateurMain.evaluer(toutesCartes);
        } else {
            resultat = evaluerAvecCartesDisponibles(toutesCartes);
        }

        Combinaison combo = resultat.getCombinaison();

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

    private ResultatMain evaluerAvecCartesDisponibles(List<Carte> cartes) {
        if (cartes.size() == 5) {
            return EvaluateurMain.evaluer(cartes);
        }
        ResultatMain meilleur = null;
        for (int i = 0; i < cartes.size(); i++) {
            List<Carte> sous = new ArrayList<>(cartes);
            sous.remove(i);
            ResultatMain r = EvaluateurMain.evaluer(sous);
            if (meilleur == null || r.compareTo(meilleur) > 0) {
                meilleur = r;
            }
        }
        return meilleur;
    }

    // ========================== UTILITAIRES ==========================

    private double calculerPotOdds(int pot, int miseActuelle) {
        if (miseActuelle == 0)
            return 1.0;
        return (double) pot / (pot + miseActuelle);
    }

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
