package com.excilys.kataspoker.web;

import com.excilys.kataspoker.evaluation.EvaluateurMain;
import com.excilys.kataspoker.evaluation.Paquet;
import com.excilys.kataspoker.game.Joueur;
import com.excilys.kataspoker.model.*;
import com.excilys.kataspoker.strategy.ContexteDecision;
import com.excilys.kataspoker.strategy.StrategieBot;
import com.excilys.kataspoker.web.dto.EtatJeuDTO;
import com.excilys.kataspoker.web.dto.EtatJeuDTO.CarteDTO;
import com.excilys.kataspoker.web.dto.EtatJeuDTO.JoueurDTO;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PokerService {

    private static final String[] NOMS_BOTS = {
            "Bot-Alice", "Bot-Bob", "Bot-Charlie", "Bot-Diana",
            "Bot-Eve", "Bot-Frank", "Bot-Grace"
    };

    private static final int CAPITAL_INITIAL = 5000;
    private static final int PRIX_BLINDE = 25;
    private static final int PRIX_GROSSE_BLINDE = PRIX_BLINDE * 2;

    private final Map<String, PartieState> parties = new ConcurrentHashMap<>();

    // ========================== ÉTAT INTERNE ==========================

    static class PartieState {
        String id;
        String mode; // SOLO_BOTS, MULTIJOUEUR
        List<Joueur> joueurs;
        Paquet paquet;
        List<Carte> board;
        int pot;
        int indexDealer;
        int manche;
        String phase; // PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN, FIN_MANCHE, FIN_PARTIE, PASSAGE_JOUEUR
        int miseActuelle;
        int joueurActifIndex;
        List<String> logActions;
        Map<Joueur, Integer> misesTour;
        int joueursActifs;
        int joueursARepondre;
        int indexCourant;
        boolean attentionActionHumain;
        boolean attentionPassageJoueur;
        boolean passageConfirme; // true after player confirms passage, prevents re-trigger
        Map<Joueur, ResultatMain> resultatsShowdown;
        String messageShowdown;
    }

    // ========================== CRÉATION ==========================

    public EtatJeuDTO creerPartie(String pseudo, int nbAdversaires, String mode) {
        PartieState state = new PartieState();
        state.id = UUID.randomUUID().toString().substring(0, 8);
        state.mode = mode;
        state.joueurs = new ArrayList<>();
        state.paquet = new Paquet();
        state.board = new ArrayList<>();
        state.pot = 0;
        state.indexDealer = 0;
        state.manche = 0;
        state.logActions = new ArrayList<>();
        state.misesTour = new HashMap<>();

        // Créer joueur humain
        state.joueurs.add(new Joueur(pseudo, null)); // pas de stratégie, c'est le web

        if ("MULTIJOUEUR".equals(mode)) {
            // Autres joueurs humains — on les a déjà passés dans le pseudo séparés par
            // virgule
            // Non, en fait on recevra les pseudos additionnels via le controller
        } else {
            // Solo vs bots
            for (int i = 0; i < nbAdversaires && i < NOMS_BOTS.length; i++) {
                state.joueurs.add(new Joueur(NOMS_BOTS[i], new StrategieBot(NOMS_BOTS[i], false)));
            }
        }

        parties.put(state.id, state);
        return buildEtatLobby(state);
    }

    public EtatJeuDTO creerPartieMultijoueur(List<String> pseudos) {
        PartieState state = new PartieState();
        state.id = UUID.randomUUID().toString().substring(0, 8);
        state.mode = "MULTIJOUEUR";
        state.joueurs = new ArrayList<>();
        state.paquet = new Paquet();
        state.board = new ArrayList<>();
        state.pot = 0;
        state.indexDealer = 0;
        state.manche = 0;
        state.logActions = new ArrayList<>();
        state.misesTour = new HashMap<>();

        for (String pseudo : pseudos) {
            state.joueurs.add(new Joueur(pseudo.trim(), null));
        }

        parties.put(state.id, state);
        return buildEtatLobby(state);
    }

    // ========================== NOUVELLE MANCHE ==========================

    public EtatJeuDTO lancerManche(String partieId) {
        PartieState state = parties.get(partieId);
        if (state == null)
            return null;

        state.manche++;
        state.paquet.reset();
        state.paquet.shuffle();
        state.board.clear();
        state.pot = 0;
        state.logActions.clear();
        state.resultatsShowdown = null;
        state.messageShowdown = null;

        for (Joueur j : state.joueurs) {
            j.clearHand();
            j.setAction(null);
            j.resetEtatAllIn();
        }

        // Distribuer 2 cartes par joueur
        for (int i = 0; i < 2 * state.joueurs.size(); i++) {
            Carte carte = state.paquet.piocher();
            state.joueurs.get(i % state.joueurs.size()).getHand().addHand(carte);
        }

        // Blindes
        int nbJ = state.joueurs.size();
        Joueur petiteBlinde = state.joueurs.get((state.indexDealer + 1) % nbJ);
        Joueur grosseBlinde = state.joueurs.get((state.indexDealer + 2) % nbJ);

        int pbMontant = Math.min(PRIX_BLINDE, petiteBlinde.getCapital());
        petiteBlinde.miser(pbMontant);
        state.pot += pbMontant;

        int gbMontant = Math.min(PRIX_GROSSE_BLINDE, grosseBlinde.getCapital());
        grosseBlinde.miser(gbMontant);
        state.pot += gbMontant;

        state.logActions.add(petiteBlinde.getPseudo() + " → petite blinde (" + pbMontant + ")");
        state.logActions.add(grosseBlinde.getPseudo() + " → grosse blinde (" + gbMontant + ")");

        // Commencer le pré-flop
        state.phase = "PRE_FLOP";
        state.miseActuelle = PRIX_GROSSE_BLINDE;
        initTourDeMise(state, (state.indexDealer + 3) % nbJ);

        return avancerJeu(state);
    }

    // ========================== ACTION DU JOUEUR ==========================

    public EtatJeuDTO jouerAction(String partieId, String actionStr, int montant) {
        PartieState state = parties.get(partieId);
        if (state == null || !state.attentionActionHumain)
            return null;

        Joueur joueur = state.joueurs.get(state.joueurActifIndex);
        Actions action = Actions.valueOf(actionStr);

        executerAction(state, joueur, action, montant);
        state.attentionActionHumain = false;

        return avancerJeu(state);
    }

    public EtatJeuDTO confirmerPassage(String partieId) {
        PartieState state = parties.get(partieId);
        if (state == null || !state.attentionPassageJoueur)
            return null;

        state.attentionPassageJoueur = false;
        state.passageConfirme = true;
        return avancerJeu(state);
    }

    // ========================== LOGIQUE DE JEU ==========================

    private void initTourDeMise(PartieState state, int indexPremier) {
        state.misesTour = new HashMap<>();
        for (Joueur j : state.joueurs) {
            state.misesTour.put(j, 0);
        }
        state.joueursActifs = (int) state.joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER && !j.getEtatAllIn())
                .count();
        state.joueursARepondre = state.joueursActifs;
        state.indexCourant = indexPremier;
    }

    /**
     * Avance le jeu automatiquement :
     * - Les bots jouent tout seuls
     * - S'arrête quand c'est au tour d'un humain, ou en fin de phase/manche
     */
    private EtatJeuDTO avancerJeu(PartieState state) {

        while (true) {
            // Vérifier s'il ne reste qu'un joueur actif → fin de manche
            long restants = state.joueurs.stream()
                    .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                    .count();
            if (restants <= 1) {
                return finMancheFold(state);
            }

            // Tour de mise terminé → passer à la phase suivante
            if (state.joueursARepondre <= 0) {
                String prochainePhase = phaseSuivante(state.phase);
                if (prochainePhase == null) {
                    return showdown(state);
                }
                state.phase = prochainePhase;
                distribuerCartesBoard(state);
                state.miseActuelle = 0;
                int nbJ = state.joueurs.size();
                initTourDeMise(state, (state.indexDealer + 1) % nbJ);
                continue;
            }

            // Trouver le prochain joueur qui doit jouer
            int nbJ = state.joueurs.size();
            Joueur joueur = state.joueurs.get(state.indexCourant % nbJ);

            // Passer les joueurs éliminés, couchés ou all-in
            if (joueur.isElimine() || joueur.getEtatAllIn() || joueur.getAction() == Actions.PASSER) {
                state.indexCourant++;
                continue;
            }

            state.joueurActifIndex = state.indexCourant % nbJ;

            if (joueur.isBot()) {
                // Le bot joue automatiquement
                ContexteDecision ctx = construireContexte(state, joueur);
                StrategieBot botStrategie = (StrategieBot) joueur.getStrategie();

                Actions action = botStrategie.choisirAction(ctx, state.miseActuelle);
                int montant = 0;
                if (action == Actions.MISER || action == Actions.RELANCER) {
                    montant = botStrategie.choisirMise(ctx, state.miseActuelle);
                }

                executerAction(state, joueur, action, montant);
                continue;
            } else {
                // Joueur humain — en mode multijoueur, écran de passage d'abord
                if ("MULTIJOUEUR".equals(state.mode) && !state.passageConfirme) {
                    state.attentionPassageJoueur = true;
                    return buildEtatPassage(state, joueur);
                }
                state.passageConfirme = false; // reset for next time
                state.attentionActionHumain = true;
                return buildEtat(state, state.joueurActifIndex);
            }
        }
    }

    private void executerAction(PartieState state, Joueur joueur, Actions action, int montant) {
        joueur.setAction(action);
        String logMsg;

        switch (action) {
            case CHECKER:
                logMsg = joueur.getPseudo() + " checke";
                state.joueursARepondre--;
                break;

            case MISER:
                int mise = Math.max(montant, state.miseActuelle + 1);
                mise = Math.min(mise, joueur.getCapital());
                joueur.miser(mise);
                state.pot += mise;
                state.misesTour.put(joueur, mise);
                state.miseActuelle = mise;
                state.joueursARepondre = state.joueursActifs - 1;
                logMsg = joueur.getPseudo() + " mise " + mise;
                break;

            case SUIVRE:
                int dejaMis = state.misesTour.getOrDefault(joueur, 0);
                int aPayer = state.miseActuelle - dejaMis;
                if (aPayer > 0) {
                    int paye = joueur.miser(aPayer);
                    state.pot += paye;
                    state.misesTour.put(joueur, state.miseActuelle);
                }
                state.joueursARepondre--;
                logMsg = joueur.getPseudo() + " suit (" + state.miseActuelle + ")";
                break;

            case RELANCER:
                int nouvelleMise = Math.max(montant, state.miseActuelle + 1);
                nouvelleMise = Math.min(nouvelleMise, joueur.getCapital());
                int dejaMisR = state.misesTour.getOrDefault(joueur, 0);
                int aPayerR = nouvelleMise - dejaMisR;
                if (aPayerR > 0) {
                    int payeR = joueur.miser(aPayerR);
                    state.pot += payeR;
                }
                state.miseActuelle = nouvelleMise;
                state.misesTour.put(joueur, nouvelleMise);
                state.joueursARepondre = state.joueursActifs - 1;
                logMsg = joueur.getPseudo() + " relance à " + nouvelleMise;
                break;

            case PASSER:
                state.joueursActifs--;
                state.joueursARepondre--;
                logMsg = joueur.getPseudo() + " se couche";
                break;

            default:
                logMsg = joueur.getPseudo() + " : action inconnue";
                break;
        }

        if (joueur.isBot()) {
            logMsg = "🤖 " + logMsg;
        }
        state.logActions.add(logMsg);
        state.indexCourant++;
    }

    private String phaseSuivante(String phase) {
        return switch (phase) {
            case "PRE_FLOP" -> "FLOP";
            case "FLOP" -> "TURN";
            case "TURN" -> "RIVER";
            case "RIVER" -> null; // showdown
            default -> null;
        };
    }

    private void distribuerCartesBoard(PartieState state) {
        int nbCartes = switch (state.phase) {
            case "FLOP" -> 3;
            case "TURN", "RIVER" -> 1;
            default -> 0;
        };
        for (int i = 0; i < nbCartes; i++) {
            state.board.add(state.paquet.piocher());
        }
    }

    private EtatJeuDTO showdown(PartieState state) {
        state.phase = "SHOWDOWN";
        List<Joueur> actifs = state.joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                .collect(Collectors.toList());

        Map<Joueur, ResultatMain> resultats = new HashMap<>();
        for (Joueur j : actifs) {
            List<Carte> cartes7 = new ArrayList<>();
            cartes7.addAll(j.getHand().getHand());
            cartes7.addAll(state.board);
            resultats.put(j, EvaluateurMain.evaluer(cartes7));
        }

        ResultatMain meilleur = null;
        for (ResultatMain r : resultats.values()) {
            if (meilleur == null || r.compareTo(meilleur) > 0)
                meilleur = r;
        }

        final ResultatMain mf = meilleur;
        List<Joueur> gagnants = actifs.stream()
                .filter(j -> resultats.get(j).compareTo(mf) == 0)
                .collect(Collectors.toList());

        StringBuilder msg = new StringBuilder();
        if (gagnants.size() == 1) {
            Joueur g = gagnants.get(0);
            g.gagnerPot(state.pot);
            msg.append("🏆 ").append(g.getPseudo()).append(" remporte ")
                    .append(state.pot).append(" jetons avec ").append(meilleur.getCombinaison().getNom()).append(" !");
        } else {
            int part = state.pot / gagnants.size();
            msg.append("🤝 Égalité ! Split du pot : ");
            for (Joueur g : gagnants) {
                g.gagnerPot(part);
                msg.append(g.getPseudo()).append(" (+").append(part).append(") ");
            }
        }

        state.resultatsShowdown = resultats;
        state.messageShowdown = msg.toString();
        state.logActions.add(msg.toString());

        // Éliminer les joueurs à 0
        for (Joueur j : state.joueurs) {
            if (j.getCapital() <= 0 && !j.isElimine()) {
                j.setElimine(true);
                state.logActions.add("💀 " + j.getPseudo() + " est éliminé !");
            }
        }

        // Avancer le dealer
        state.indexDealer = (state.indexDealer + 1) % state.joueurs.size();

        // Vérifier fin de partie
        long enVie = state.joueurs.stream().filter(j -> !j.isElimine()).count();
        if (enVie <= 1) {
            state.phase = "FIN_PARTIE";
            Joueur gagnant = state.joueurs.stream().filter(j -> !j.isElimine()).findFirst().orElse(null);
            if (gagnant != null) {
                state.messageShowdown = "🏆 " + gagnant.getPseudo() + " remporte la partie avec " + gagnant.getCapital()
                        + " jetons !";
            }
        }

        return buildEtatShowdown(state);
    }

    private EtatJeuDTO finMancheFold(PartieState state) {
        state.phase = "FIN_MANCHE";
        List<Joueur> actifs = state.joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER)
                .collect(Collectors.toList());

        if (actifs.size() == 1) {
            Joueur g = actifs.get(0);
            g.gagnerPot(state.pot);
            state.messageShowdown = "🏆 " + g.getPseudo() + " remporte " + state.pot
                    + " jetons (tout le monde s'est couché)";
            state.logActions.add(state.messageShowdown);
        }

        state.indexDealer = (state.indexDealer + 1) % state.joueurs.size();

        for (Joueur j : state.joueurs) {
            if (j.getCapital() <= 0 && !j.isElimine()) {
                j.setElimine(true);
                state.logActions.add("💀 " + j.getPseudo() + " est éliminé !");
            }
        }

        long enVie = state.joueurs.stream().filter(j -> !j.isElimine()).count();
        if (enVie <= 1) {
            state.phase = "FIN_PARTIE";
        }

        return buildEtatShowdown(state);
    }

    // ========================== UTILITAIRES ==========================

    private ContexteDecision construireContexte(PartieState state, Joueur joueur) {
        int nbActifs = (int) state.joueurs.stream()
                .filter(j -> !j.isElimine() && j.getAction() != Actions.PASSER).count();
        return new ContexteDecision(
                joueur.getHand().getHand(), state.board,
                joueur.getCapital(), state.pot, state.miseActuelle, nbActifs);
    }

    // ========================== BUILDERS DTO ==========================

    private EtatJeuDTO buildEtatLobby(PartieState state) {
        EtatJeuDTO dto = new EtatJeuDTO();
        dto.setPartieId(state.id);
        dto.setPhase("LOBBY");
        dto.setMode(state.mode);
        dto.setManche(0);
        dto.setJoueurs(state.joueurs.stream().map(j -> buildJoueurDTO(j, false, null)).collect(Collectors.toList()));
        dto.setLogActions(new ArrayList<>());
        return dto;
    }

    private EtatJeuDTO buildEtat(PartieState state, int joueurHumainIndex) {
        EtatJeuDTO dto = new EtatJeuDTO();
        dto.setPartieId(state.id);
        dto.setPhase(state.phase);
        dto.setMode(state.mode);
        dto.setManche(state.manche);
        dto.setPot(state.pot);
        dto.setMiseActuelle(state.miseActuelle);
        dto.setBoard(state.board.stream().map(this::toCarteDTO).collect(Collectors.toList()));
        dto.setJoueurActifIndex(joueurHumainIndex);
        dto.setActionRequise(true);
        dto.setLogActions(new ArrayList<>(state.logActions));

        // Actions disponibles
        List<String> actions = new ArrayList<>();
        if (state.miseActuelle == 0) {
            actions.add("CHECKER");
            actions.add("MISER");
        } else {
            actions.add("SUIVRE");
            actions.add("RELANCER");
        }
        actions.add("PASSER");
        dto.setActionsDisponibles(actions);

        // Joueurs — montrer les cartes uniquement au joueur actif
        List<JoueurDTO> joueurDTOs = new ArrayList<>();
        for (int i = 0; i < state.joueurs.size(); i++) {
            Joueur j = state.joueurs.get(i);
            boolean montrerCartes = (i == joueurHumainIndex) && !j.isBot();
            joueurDTOs.add(buildJoueurDTO(j, montrerCartes, null));
        }
        dto.setJoueurs(joueurDTOs);

        return dto;
    }

    private EtatJeuDTO buildEtatPassage(PartieState state, Joueur prochainJoueur) {
        EtatJeuDTO dto = new EtatJeuDTO();
        dto.setPartieId(state.id);
        dto.setPhase("PASSAGE_JOUEUR");
        dto.setMode(state.mode);
        dto.setManche(state.manche);
        dto.setPot(state.pot);
        dto.setMessage("C'est au tour de " + prochainJoueur.getPseudo());
        dto.setJoueurActifIndex(state.joueurActifIndex);
        dto.setActionRequise(false);
        dto.setBoard(state.board.stream().map(this::toCarteDTO).collect(Collectors.toList()));
        dto.setLogActions(new ArrayList<>(state.logActions));
        dto.setJoueurs(state.joueurs.stream().map(j -> buildJoueurDTO(j, false, null)).collect(Collectors.toList()));
        return dto;
    }

    private EtatJeuDTO buildEtatShowdown(PartieState state) {
        EtatJeuDTO dto = new EtatJeuDTO();
        dto.setPartieId(state.id);
        dto.setPhase(state.phase);
        dto.setMode(state.mode);
        dto.setManche(state.manche);
        dto.setPot(state.pot);
        dto.setBoard(state.board.stream().map(this::toCarteDTO).collect(Collectors.toList()));
        dto.setMessage(state.messageShowdown);
        dto.setActionRequise(false);
        dto.setLogActions(new ArrayList<>(state.logActions));

        List<JoueurDTO> joueurDTOs = new ArrayList<>();
        for (Joueur j : state.joueurs) {
            boolean montrer = !j.isElimine() && j.getAction() != Actions.PASSER;
            ResultatMain r = state.resultatsShowdown != null ? state.resultatsShowdown.get(j) : null;
            String combo = (r != null) ? r.getCombinaison().getNom() : null;
            joueurDTOs.add(buildJoueurDTO(j, montrer, combo));
        }
        dto.setJoueurs(joueurDTOs);

        return dto;
    }

    private JoueurDTO buildJoueurDTO(Joueur j, boolean montrerCartes, String combinaison) {
        JoueurDTO dto = new JoueurDTO();
        dto.setPseudo(j.getPseudo());
        dto.setCapital(j.getCapital());
        dto.setBot(j.isBot());
        dto.setElimine(j.isElimine());
        dto.setActif(j.getAction() != Actions.PASSER);
        dto.setAction(j.getAction() != null ? j.getAction().name() : null);
        dto.setCombinaison(combinaison);

        if (montrerCartes && j.getHand() != null) {
            dto.setCartes(j.getHand().getHand().stream().map(this::toCarteDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private CarteDTO toCarteDTO(Carte c) {
        String valeur = switch (c.getValeur()) {
            case AS -> "A";
            case ROI -> "K";
            case DAME -> "Q";
            case VALET -> "J";
            case DIX -> "10";
            default -> String.valueOf(c.getValeur().getRang());
        };

        String symbole = switch (c.getCouleur()) {
            case COEUR -> "♥";
            case CARREAUX -> "♦";
            case PIQUE -> "♠";
            case TREFLE -> "♣";
        };

        return new CarteDTO(valeur, c.getCouleur().name(), symbole);
    }

    public EtatJeuDTO getEtat(String partieId) {
        PartieState state = parties.get(partieId);
        if (state == null)
            return null;
        if (state.phase == null || "LOBBY".equals(state.phase))
            return buildEtatLobby(state);
        if (state.attentionActionHumain)
            return buildEtat(state, state.joueurActifIndex);
        return buildEtatShowdown(state);
    }
}
