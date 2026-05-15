# Katas Poker — Texas Hold'em

Application de poker Texas Hold'em en Java / Spring Boot avec interface web et intelligence artificielle.

## Fonctionnalités

- **Mode Solo** : 1 joueur humain contre 1 à 7 bots IA
- **Mode Multijoueur local** : 2 à 8 joueurs humains (passage du device)
- **Interface web** : UI sombre responsive servie en static par Spring Boot
- **Ligne de commande** : entrée interactive colorisée (ANSI + émojis)
- **IA stratégique** : évaluation pré-flop / post-flop, calcul des pot odds, bluff (10 %)
- **Évaluation complète** : 10 combinaisons, meilleure main sur 7 cartes (2 en main + 5 au board)

## Stack technique

| Couche | Technologie |
|---|---|
| Backend | Java 21, Spring Boot 3.2.3 |
| REST | Spring MVC (`spring-boot-starter-web`) |
| Frontend | HTML / CSS / JS vanilla |
| Tests | JUnit 5 |
| Build | Maven |
| Conteneur | Docker (multi-stage), Docker Compose |

## Architecture

```
com.excilys.kataspoker/
├── model/          → Carte, Couleur, Valeurs, Hand, Actions, Combinaison
├── evaluation/     → EvaluateurMain (sélection meilleure main C(7,5)), Paquet
├── strategy/       → StrategieJoueur (interface), StrategieBot, StrategieHumaine
├── game/           → Table (rounds, blinds, enchères, showdown), Joueur
└── web/
    ├── GameController.java   → Endpoints REST /api/game/*
    ├── PokerService.java     → Orchestration de l'état de jeu
    └── dto/EtatJeuDTO.java   → Réponses JSON
```

Le projet applique le **pattern Strategy** pour découpler la prise de décision (humain ou bot) du moteur de jeu.

## Déroulement d'une partie

1. Rotation du bouton dealer
2. Blinds (small 25, big 50)
3. Distribution de 2 cartes privées
4. 4 tours d'enchères : **PRE_FLOP → FLOP → TURN → RIVER**
5. Showdown ou victoire par abandon
6. Distribution du pot, élimination des joueurs à 0 jeton

### Actions disponibles

| Action | Description |
|---|---|
| `CHECKER` | Passer sans miser (si aucune mise en cours) |
| `MISER` | Ouvrir les enchères |
| `SUIVRE` | Égaler la mise courante |
| `RELANCER` | Surenchérir |
| `PASSER` | Se coucher (fold) |

## Endpoints REST

| Méthode | Route | Description |
|---|---|---|
| `GET` | `/api/game/health` | Healthcheck |
| `POST` | `/api/game/new` | Créer une partie (`SOLO_BOTS` ou `MULTIJOUEUR`) |
| `GET` | `/api/game/{id}` | État courant de la partie |
| `POST` | `/api/game/{id}/next` | Lancer le tour suivant |
| `POST` | `/api/game/{id}/action` | Jouer une action (`action` + `montant`) |
| `POST` | `/api/game/{id}/ready` | Confirmer le passage (multijoueur) |

## Lancer le projet

### Avec Maven

```bash
./mvnw spring-boot:run
```

L'application est accessible sur [http://localhost:8080](http://localhost:8080).

### Avec Docker

```bash
docker compose up --build
```

Le build est multi-stage : Maven compile dans un conteneur JDK 21, le JAR est copié dans une image JRE 21 légère (Eclipse Temurin).

Un healthcheck vérifie `/api/game/health` toutes les 30 secondes.

### Mode CLI

```bash
./mvnw compile exec:java -Dexec.mainClass="com.excilys.kataspoker.Main"
```

## Tests

```bash
./mvnw test
```

- `EvaluateurMainTest` — vérifie les 10 combinaisons poker
- `StrategieBotTest` — valide les décisions IA (fold sur main faible, bet sur AA/KK/AK)

## Combinaisons reconnues

| Force | Combinaison |
|---|---|
| 9 | Quinte flush royale |
| 8 | Quinte flush |
| 7 | Carré |
| 6 | Full house |
| 5 | Couleur (flush) |
| 4 | Suite (straight) |
| 3 | Brelan |
| 2 | Double paire |
| 1 | Paire |
| 0 | Carte haute |

## IA — StrategieBot

La stratégie du bot se déroule en deux phases :

- **Pré-flop** : score de force basé sur les paires de poche, les cartes hautes, les connecteurs assortis et l'écart entre les deux cartes.
- **Post-flop** : appel à `EvaluateurMain` pour évaluer la main actuelle sur le board.

Le bot calcule ensuite les **pot odds**, applique un facteur de bluff (10 %) et choisit son action. Le délai de réflexion simulé (800–1 300 ms) peut être désactivé.
