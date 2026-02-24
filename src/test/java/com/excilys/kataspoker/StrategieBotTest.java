package com.excilys.kataspoker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

class StrategieBotTest {

    private Carte carte(Valeurs v, Couleur c) {
        return new Carte(v, c);
    }

    // ========================== CHOIX D'ACTION ==========================

    @Test
    void testBotCheckeAvecMainFaibleSansMise() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.DEUX, Couleur.COEUR), carte(Valeurs.SEPT, Couleur.PIQUE)),
                Collections.emptyList(), // pré-flop
                5000, 100, 0, 3);

        // Sans mise, un bot avec 2-7 (pire main) devrait checker la plupart du temps
        int checks = 0;
        for (int i = 0; i < 50; i++) {
            Actions action = bot.choisirAction(ctx, 0);
            if (action == Actions.CHECKER)
                checks++;
        }
        assertTrue(checks > 30, "Le bot devrait checker majoritairement avec 2-7 (a checké " + checks + "/50 fois)");
    }

    @Test
    void testBotMiseAvecMainForte() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.AS, Couleur.PIQUE)),
                Collections.emptyList(), // pré-flop
                5000, 100, 0, 3);

        // Avec AA (main la plus forte), le bot devrait miser très souvent
        int mises = 0;
        for (int i = 0; i < 50; i++) {
            Actions action = bot.choisirAction(ctx, 0);
            if (action == Actions.MISER)
                mises++;
        }
        assertTrue(mises > 35, "Le bot devrait miser très souvent avec AA (a misé " + mises + "/50 fois)");
    }

    @Test
    void testBotPasseAvecMainFaibleEtGrosseMise() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.DEUX, Couleur.COEUR), carte(Valeurs.SEPT, Couleur.PIQUE)),
                Arrays.asList(
                        carte(Valeurs.ROI, Couleur.CARREAUX),
                        carte(Valeurs.DAME, Couleur.TREFLE),
                        carte(Valeurs.VALET, Couleur.COEUR)), // post-flop, rien qui s'associe
                5000, 500, 200, 3);

        // Avec 2-7 et un board KQJ sans rien, face à une mise, devrait passer souvent
        int passes = 0;
        for (int i = 0; i < 50; i++) {
            Actions action = bot.choisirAction(ctx, 200);
            if (action == Actions.PASSER)
                passes++;
        }
        assertTrue(passes > 25,
                "Le bot devrait souvent passer avec 2-7 face à une mise (a passé " + passes + "/50 fois)");
    }

    @Test
    void testBotSuitAvecMainCorrecte() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.ROI, Couleur.COEUR), carte(Valeurs.ROI, Couleur.PIQUE)),
                Arrays.asList(
                        carte(Valeurs.ROI, Couleur.CARREAUX),
                        carte(Valeurs.CINQ, Couleur.TREFLE),
                        carte(Valeurs.DEUX, Couleur.COEUR)), // post-flop, brelan de rois !
                5000, 500, 100, 3);

        // Avec un brelan de rois et une mise à suivre, devrait suivre ou relancer (pas
        // passer)
        int agressif = 0;
        for (int i = 0; i < 50; i++) {
            Actions action = bot.choisirAction(ctx, 100);
            if (action == Actions.SUIVRE || action == Actions.RELANCER)
                agressif++;
        }
        assertTrue(agressif > 35,
                "Le bot devrait suivre/relancer avec un brelan de Rois (a agi agressivement " + agressif + "/50 fois)");
    }

    // ========================== CHOIX DE MISE ==========================

    @Test
    void testMiseEstValide() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.ROI, Couleur.PIQUE)),
                Collections.emptyList(),
                5000, 200, 100, 3);

        for (int i = 0; i < 20; i++) {
            int mise = bot.choisirMise(ctx, 100);
            assertTrue(mise > 100, "La mise doit être > à la mise minimale (100), got " + mise);
            assertTrue(mise <= 5000, "La mise ne peut pas dépasser le capital (5000), got " + mise);
        }
    }

    @Test
    void testMiseGrosBetAvecMainForte() {
        StrategieBot bot = new StrategieBot("TestBot", false);
        ContexteDecision ctx = new ContexteDecision(
                Arrays.asList(carte(Valeurs.AS, Couleur.COEUR), carte(Valeurs.AS, Couleur.PIQUE)),
                Collections.emptyList(),
                5000, 200, 50, 3);

        // Avec AA, les mises devraient être significatives
        int totalMise = 0;
        int nbTests = 20;
        for (int i = 0; i < nbTests; i++) {
            totalMise += bot.choisirMise(ctx, 50);
        }
        double moyenneMise = (double) totalMise / nbTests;
        assertTrue(moyenneMise > 500, "La mise moyenne avec AA devrait être élevée, got " + moyenneMise);
    }
}
