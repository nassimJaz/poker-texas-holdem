package com.excilys.kataspoker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class EvaluateurMainTest {

    // ========================== HELPERS ==========================

    private Carte carte(Valeurs v, Couleur c) {
        return new Carte(v, c);
    }

    private List<Carte> main7(Carte... cartes) {
        return Arrays.asList(cartes);
    }

    // ========================== DÉTECTION COMBINAISONS ==========================

    @Test
    void testHauteCarte() {
        List<Carte> cartes = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX),
                carte(Valeurs.SEPT, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.HAUTE_CARTE, resultat.getCombinaison());
    }

    @Test
    void testPaire() {
        List<Carte> cartes = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX),
                carte(Valeurs.SEPT, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.PAIRE, resultat.getCombinaison());
    }

    @Test
    void testDoublePaire() {
        List<Carte> cartes = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.DOUBLE_PAIRE, resultat.getCombinaison());
    }

    @Test
    void testBrelan() {
        List<Carte> cartes = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.SEPT, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.BRELAN, resultat.getCombinaison());
    }

    @Test
    void testQuinte() {
        List<Carte> cartes = main7(
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.HUIT, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.SIX, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.QUINTE, resultat.getCombinaison());
    }

    @Test
    void testQuinteRoue() {
        // A-2-3-4-5 (la roue / quinte basse)
        List<Carte> cartes = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX),
                carte(Valeurs.QUATRE, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.NEUF, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.QUINTE, resultat.getCombinaison());
    }

    @Test
    void testCouleurFlush() {
        List<Carte> cartes = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.DIX, Couleur.COEUR),
                carte(Valeurs.HUIT, Couleur.COEUR),
                carte(Valeurs.SIX, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.COULEUR_FLUSH, resultat.getCombinaison());
    }

    @Test
    void testFull() {
        List<Carte> cartes = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.FULL, resultat.getCombinaison());
    }

    @Test
    void testCarre() {
        List<Carte> cartes = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.ROI, Couleur.TREFLE),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.CARRE, resultat.getCombinaison());
    }

    @Test
    void testQuinteFlush() {
        List<Carte> cartes = main7(
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.HUIT, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.SIX, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.QUINTE_FLUSH, resultat.getCombinaison());
    }

    @Test
    void testQuinteFlushRoyale() {
        List<Carte> cartes = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.DAME, Couleur.COEUR),
                carte(Valeurs.VALET, Couleur.COEUR),
                carte(Valeurs.DIX, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        ResultatMain resultat = EvaluateurMain.evaluer(cartes);
        assertEquals(Combinaison.QUINTE_FLUSH_ROYALE, resultat.getCombinaison());
    }

    // ========================== COMPARAISONS ==========================

    @Test
    void testFullBatFlush() {
        List<Carte> full = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.NEUF, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        List<Carte> flush = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.DIX, Couleur.COEUR),
                carte(Valeurs.HUIT, Couleur.COEUR),
                carte(Valeurs.SIX, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.PIQUE));
        ResultatMain rFull = EvaluateurMain.evaluer(full);
        ResultatMain rFlush = EvaluateurMain.evaluer(flush);
        assertTrue(rFull.compareTo(rFlush) > 0, "Un full doit battre une couleur");
    }

    @Test
    void testPaireDeRoisAvecAsKickerBatPaireDeRoisAvecDameKicker() {
        List<Carte> mainAs = main7(
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.PIQUE),
                carte(Valeurs.AS, Couleur.CARREAUX),
                carte(Valeurs.SEPT, Couleur.TREFLE),
                carte(Valeurs.CINQ, Couleur.COEUR),
                carte(Valeurs.TROIS, Couleur.PIQUE),
                carte(Valeurs.DEUX, Couleur.CARREAUX));
        List<Carte> mainDame = main7(
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.ROI, Couleur.TREFLE),
                carte(Valeurs.DAME, Couleur.COEUR),
                carte(Valeurs.SEPT, Couleur.PIQUE),
                carte(Valeurs.CINQ, Couleur.CARREAUX),
                carte(Valeurs.TROIS, Couleur.TREFLE),
                carte(Valeurs.DEUX, Couleur.COEUR));
        ResultatMain rAs = EvaluateurMain.evaluer(mainAs);
        ResultatMain rDame = EvaluateurMain.evaluer(mainDame);
        assertTrue(rAs.compareTo(rDame) > 0, "Paire de Rois + As kicker doit battre Paire de Rois + Dame kicker");
    }

    @Test
    void testEgaliteParfaite() {
        // Mêmes rangs, couleurs différentes -> égalité
        List<Carte> main1 = main7(
                carte(Valeurs.AS, Couleur.COEUR),
                carte(Valeurs.ROI, Couleur.COEUR),
                carte(Valeurs.NEUF, Couleur.PIQUE),
                carte(Valeurs.SEPT, Couleur.CARREAUX),
                carte(Valeurs.CINQ, Couleur.TREFLE),
                carte(Valeurs.TROIS, Couleur.COEUR),
                carte(Valeurs.DEUX, Couleur.PIQUE));
        List<Carte> main2 = main7(
                carte(Valeurs.AS, Couleur.PIQUE),
                carte(Valeurs.ROI, Couleur.CARREAUX),
                carte(Valeurs.NEUF, Couleur.TREFLE),
                carte(Valeurs.SEPT, Couleur.COEUR),
                carte(Valeurs.CINQ, Couleur.PIQUE),
                carte(Valeurs.TROIS, Couleur.CARREAUX),
                carte(Valeurs.DEUX, Couleur.TREFLE));
        ResultatMain r1 = EvaluateurMain.evaluer(main1);
        ResultatMain r2 = EvaluateurMain.evaluer(main2);
        assertEquals(0, r1.compareTo(r2), "Les deux mains doivent être à égalité");
    }
}
