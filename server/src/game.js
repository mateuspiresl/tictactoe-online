import { log } from './utils';


const WINNING_PATTERNS = new Set([
  '111000000',
  '000111000',
  '000000111',
  '100100100',
  '010010010',
  '001001001',
  '100010001',
  '001010100',
]);


/**
 * Game representation.
 */
export default class Game {
  /**
   * Game constructor.
   * @param {Player} playerA First player.
   * @param {Player} playerB Second player.
   */
  constructor(playerA, playerB) {
    this.players = [playerA, playerB];
    this.turn = 0;
    this.board = [
      [null, null, null],
      [null, null, null],
      [null, null, null],
    ];
    this.winner = null;
  }

  /**
   * Starts the player's clients.
   */
  start() {
    this._startPlayer(0, 1);
    this._startPlayer(1, 0);
  }

  _startPlayer(selfId, opponentId) {
    const self = this.players[selfId];
    const opponent = this.players[opponentId];

    // Listen to movements
    self.listen((movement) => {
      log(self.socket.id, self.name, 'Movement', movement);

      if (this.winner === null) {
        try {
          this._move(selfId, opponentId, movement);

          // Check if the last movement makes a winning pattern
          if (this._hasWinner(selfId)) {
            log(self.socket.id, self.name, 'Winner, board:', this.board);

            // Holds the winner to avoid making moves from now
            this.winner = selfId;

            // Notify the winner and disconnect the players
            self.notifyWinner(this.board, selfId);
            opponent.notifyWinner(this.board, selfId);
          } else {
            // Sends the current state to the players
            this._update();
          }
        } catch (error) {
          log(self.socket.id, self.name, error);
          self.notifyError(error);

          // Sends the current state to ensure the clients have it
          this._update();
        }
      } else {
        // When there is a winner, the players are disconnected, so this case should not happen
        // If it does, there's nothing to do, just log
        log(self.socket.id, self.name, 'Made a movement when there was a winner');
      }
    });

    // Listen to disconnections
    self.listenDisconnection(() => {
      // Notify the disconnection to the players and disconnect them
      self.notifyClose(selfId);
      opponent.notifyClose(selfId);
    });

    // Starts the game
    self.start(
      selfId,
      opponent.name,
      this.board,
      this.turn,
    );
  }

  _update() {
    this.players[0].update(this.board, this.turn);
    this.players[1].update(this.board, this.turn);
  }

  _move(selfId, opponentId, { line, column }) {
    if (this.turn !== selfId) {
      throw new Error('Not player\'s turn, movement ignored');
    }

    if (line < 0 || line > 2 || column < 0 || column > 2) {
      throw new Error(`Cell ${line},${column} doesn't exist, movement ignored`);
    }

    if (this.board[line][column] !== null) {
      throw new Error(`Cell ${line},${column} is marked, movement ignored`);
    }

    this.board[line][column] = selfId;
    this.turn = opponentId;
  }

  _hasWinner(selfId) {
    const pattern = this.board.map(
      line => line.map(
        cell => (cell === selfId ? 1 : 0),
      ).join(''),
    ).join('');

    return WINNING_PATTERNS.has(pattern);
  }
}
