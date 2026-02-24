// ================================================
// POKER — Frontend Logic
// ================================================

let gameState = null;
let currentMode = 'SOLO_BOTS';
let nbBots = 3;
let nbPlayers = 2;
let pendingBetAction = null; // 'MISER' ou 'RELANCER'

// ==================== LOBBY ====================

function selectMode(mode) {
    currentMode = mode;
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelector(`[data-mode="${mode}"]`).classList.add('active');
    document.getElementById('form-solo').classList.toggle('hidden', mode !== 'SOLO_BOTS');
    document.getElementById('form-multi').classList.toggle('hidden', mode !== 'MULTIJOUEUR');
    if (mode === 'MULTIJOUEUR') updatePseudoInputs();
}

function changeBots(delta) {
    nbBots = Math.max(1, Math.min(7, nbBots + delta));
    document.getElementById('nb-bots').textContent = nbBots;
}

function changePlayers(delta) {
    nbPlayers = Math.max(2, Math.min(8, nbPlayers + delta));
    document.getElementById('nb-players').textContent = nbPlayers;
    updatePseudoInputs();
}

function updatePseudoInputs() {
    const container = document.getElementById('pseudo-inputs');
    container.innerHTML = '';
    for (let i = 0; i < nbPlayers; i++) {
        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = `Joueur ${i + 1}`;
        input.maxLength = 15;
        input.id = `pseudo-multi-${i}`;
        container.appendChild(input);
    }
}

async function startSoloBots() {
    const pseudo = document.getElementById('pseudo-solo').value.trim() || 'Joueur';
    const resp = await fetch('/api/game/new', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pseudo, nbAdversaires: nbBots, mode: 'SOLO_BOTS' })
    });
    gameState = await resp.json();
    hideScreen('lobby');
    showScreen('game');
    startRound();
}

async function startMultiplayer() {
    const pseudos = [];
    for (let i = 0; i < nbPlayers; i++) {
        const val = document.getElementById(`pseudo-multi-${i}`).value.trim();
        pseudos.push(val || `Joueur ${i + 1}`);
    }
    const resp = await fetch('/api/game/new', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pseudos, mode: 'MULTIJOUEUR' })
    });
    gameState = await resp.json();
    hideScreen('lobby');
    showScreen('game');
    startRound();
}

// ==================== GAME FLOW ====================

async function startRound() {
    const resp = await fetch(`/api/game/${gameState.partieId}/next`, { method: 'POST' });
    gameState = await resp.json();
    renderState();
}

async function playAction(action, montant) {
    const resp = await fetch(`/api/game/${gameState.partieId}/action`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action, montant: montant || 0 })
    });
    gameState = await resp.json();
    renderState();
}

async function confirmPassage() {
    const resp = await fetch(`/api/game/${gameState.partieId}/ready`, { method: 'POST' });
    gameState = await resp.json();
    hideScreen('passage');
    renderState();
}

async function nextRound() {
    hideScreen('showdown');
    startRound();
}

// ==================== RENDER ====================

function renderState() {
    if (!gameState) return;

    const phase = gameState.phase;

    if (phase === 'PASSAGE_JOUEUR') {
        document.getElementById('passage-message').textContent = gameState.message;
        showScreen('passage');
        return;
    }

    if (phase === 'SHOWDOWN' || phase === 'FIN_MANCHE') {
        renderShowdown();
        return;
    }

    if (phase === 'FIN_PARTIE') {
        renderEndgame();
        return;
    }

    // Game table
    hideScreen('lobby');
    hideScreen('passage');
    hideScreen('showdown');
    hideScreen('endgame');
    showScreen('game');

    // Header
    document.getElementById('manche-num').textContent = gameState.manche;
    document.getElementById('phase-badge').textContent = formatPhase(phase);

    // Pot
    document.getElementById('pot-amount').textContent = gameState.pot.toLocaleString();

    // Board
    const boardEl = document.getElementById('board-cards');
    boardEl.innerHTML = '';
    if (gameState.board) {
        gameState.board.forEach((c, i) => {
            const card = createCardEl(c);
            card.style.animationDelay = `${i * 0.1}s`;
            boardEl.appendChild(card);
        });
    }

    // Players bar
    renderPlayers();

    // Hand
    renderHand();

    // Actions
    renderActions();

    // Log
    renderLog();
}

function renderPlayers() {
    const bar = document.getElementById('players-bar');
    bar.innerHTML = '';
    if (!gameState.joueurs) return;

    gameState.joueurs.forEach((j, i) => {
        const el = document.createElement('div');
        el.className = 'player-chip';
        if (i === gameState.joueurActifIndex && gameState.actionRequise) el.classList.add('active-player');
        if (!j.actif) el.classList.add('folded');
        if (j.elimine) el.classList.add('eliminated');

        const icon = j.bot ? ' 🤖' : '';
        el.innerHTML = `
            <div class="player-name">${j.pseudo}${icon}</div>
            <div class="player-capital">${j.capital.toLocaleString()} ♦</div>
            ${j.action ? `<div class="player-action">${formatAction(j.action)}</div>` : ''}
        `;
        bar.appendChild(el);
    });
}

function renderHand() {
    const handCards = document.getElementById('hand-cards');
    const handLabel = document.getElementById('hand-label');
    handCards.innerHTML = '';

    const joueurActif = gameState.joueurs?.[gameState.joueurActifIndex];
    if (!joueurActif) return;

    handLabel.textContent = `Cartes de ${joueurActif.pseudo}`;

    if (joueurActif.cartes && joueurActif.cartes.length > 0) {
        joueurActif.cartes.forEach(c => {
            handCards.appendChild(createCardEl(c, false));
        });
    } else {
        handCards.innerHTML = '<span style="color: var(--text-muted); font-size: 0.85rem;">Cartes cachées</span>';
    }
}

function renderActions() {
    const panel = document.getElementById('actions-panel');
    const buttonsEl = document.getElementById('actions-buttons');
    const betArea = document.getElementById('bet-area');
    buttonsEl.innerHTML = '';
    betArea.classList.add('hidden');
    pendingBetAction = null;

    if (!gameState.actionRequise || !gameState.actionsDisponibles) {
        panel.classList.add('hidden');
        return;
    }
    panel.classList.remove('hidden');

    const joueur = gameState.joueurs[gameState.joueurActifIndex];
    const capital = joueur ? joueur.capital : 5000;

    gameState.actionsDisponibles.forEach(action => {
        const btn = document.createElement('button');
        btn.className = 'action-btn';

        switch (action) {
            case 'CHECKER':
                btn.classList.add('check');
                btn.textContent = '✓ Checker';
                btn.onclick = () => playAction('CHECKER');
                break;
            case 'MISER':
                btn.classList.add('bet');
                btn.textContent = '💰 Miser';
                btn.onclick = () => showBetSlider('MISER', gameState.miseActuelle, capital);
                break;
            case 'SUIVRE':
                btn.classList.add('check');
                btn.textContent = `→ Suivre (${gameState.miseActuelle})`;
                btn.onclick = () => playAction('SUIVRE');
                break;
            case 'RELANCER':
                btn.classList.add('raise');
                btn.textContent = '⬆ Relancer';
                btn.onclick = () => showBetSlider('RELANCER', gameState.miseActuelle, capital);
                break;
            case 'PASSER':
                btn.classList.add('fold');
                btn.textContent = '✗ Passer';
                btn.onclick = () => playAction('PASSER');
                break;
        }
        buttonsEl.appendChild(btn);
    });
}

function showBetSlider(action, miseMin, capital) {
    pendingBetAction = action;
    const betArea = document.getElementById('bet-area');
    const slider = document.getElementById('bet-slider');
    const input = document.getElementById('bet-input');

    const min = miseMin + 1;
    slider.min = min;
    slider.max = capital;
    slider.value = Math.min(Math.round(capital * 0.3), capital);
    input.min = min;
    input.max = capital;
    input.value = slider.value;

    slider.oninput = () => { input.value = slider.value; };
    input.oninput = () => { slider.value = input.value; };

    betArea.classList.remove('hidden');
}

function confirmBet() {
    if (!pendingBetAction) return;
    const montant = parseInt(document.getElementById('bet-input').value);
    playAction(pendingBetAction, montant);
}

function renderLog() {
    const logEl = document.getElementById('action-log');
    logEl.innerHTML = '';
    if (!gameState.logActions) return;

    gameState.logActions.forEach(msg => {
        const entry = document.createElement('div');
        entry.className = 'log-entry';
        entry.textContent = msg;
        logEl.appendChild(entry);
    });
    logEl.scrollTop = logEl.scrollHeight;
}

// ==================== SHOWDOWN ====================

function renderShowdown() {
    showScreen('showdown');

    const titleEl = document.getElementById('showdown-title');
    titleEl.textContent = gameState.phase === 'SHOWDOWN' ? 'Showdown' : 'Fin de manche';

    // Board
    const boardEl = document.getElementById('showdown-board');
    boardEl.innerHTML = '';
    if (gameState.board) {
        gameState.board.forEach(c => boardEl.appendChild(createCardEl(c, true)));
    }

    // Players
    const playersEl = document.getElementById('showdown-players');
    playersEl.innerHTML = '';
    if (gameState.joueurs) {
        gameState.joueurs.forEach(j => {
            if (j.elimine && !j.cartes) return;
            const el = document.createElement('div');
            el.className = 'showdown-player';
            if (gameState.message && gameState.message.includes(j.pseudo) && gameState.message.includes('🏆')) {
                el.classList.add('winner');
            }

            let cardsHtml = '';
            if (j.cartes && j.cartes.length > 0) {
                cardsHtml = '<div class="sp-cards">' +
                    j.cartes.map(c => createCardEl(c, true).outerHTML).join('') +
                    '</div>';
            }

            const icon = j.bot ? ' 🤖' : '';
            el.innerHTML = `
                <span class="sp-name">${j.pseudo}${icon}</span>
                ${cardsHtml}
                <span class="sp-combo">${j.combinaison || (j.actif ? '' : 'couché')}</span>
                <span class="sp-capital">${j.capital.toLocaleString()} ♦</span>
            `;
            playersEl.appendChild(el);
        });
    }

    // Message
    document.getElementById('showdown-message').textContent = gameState.message || '';

    // Check if game over
    if (gameState.phase === 'FIN_PARTIE') {
        renderEndgame();
    }
}

function renderEndgame() {
    hideScreen('showdown');
    showScreen('endgame');
    document.getElementById('endgame-message').textContent = gameState.message || 'La partie est terminée !';
}

// ==================== HELPERS ====================

function createCardEl(c, mini) {
    const el = document.createElement('div');
    el.className = 'card' + (mini ? ' mini' : '');

    const isRed = c.symbole === '♥' || c.symbole === '♦';
    el.classList.add(isRed ? 'red' : 'black');

    el.innerHTML = `<span class="card-value">${c.valeur}</span><span class="card-suit">${c.symbole}</span>`;
    return el;
}

function formatPhase(phase) {
    const map = {
        'PRE_FLOP': 'Pré-Flop',
        'FLOP': 'Flop',
        'TURN': 'Turn',
        'RIVER': 'River',
        'SHOWDOWN': 'Showdown'
    };
    return map[phase] || phase;
}

function formatAction(action) {
    const map = {
        'CHECKER': '✓ Check',
        'MISER': '💰 Mise',
        'SUIVRE': '→ Suit',
        'RELANCER': '⬆ Relance',
        'PASSER': '✗ Couché'
    };
    return map[action] || action;
}

function showScreen(id) {
    document.getElementById(id).classList.add('active');
}

function hideScreen(id) {
    document.getElementById(id).classList.remove('active');
}

// Init multiplayer pseudo inputs
updatePseudoInputs();
