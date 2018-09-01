import { log } from './utils';


/**
 * Player representation.
 */
export default class Player {
  /**
   * Player constructor.
   * @param {SocketIO} socket The client.
   * @param {String} name The name.
   */
  constructor(socket, name) {
    this.socket = socket;
    this.name = name;
    this.disconnected = false;
  }

  /**
   * Sends the game information and the current state.
   * @param {Number} id The id.''
   * @param {String} opponent The opponent name.
   * @param {Array<Array<*>>} board The board.
   * @param {Number} turn The player in turn.
   */
  start(id, opponent, board, turn) {
    this.socket.emit('start', {
      id, opponent, board, turn,
    });
  }

  /**
   * Sends the current state.
   * @param {Array<Array<*>>} board The board.
   * @param {Number} turn The player in turn.
   */
  update(board, turn) {
    this.socket.emit('state', { board, turn });
  }

  /**
   * Notify the player is waiting for a match.
   */
  notifyWaiting() {
    this.socket.emit('waiting', {});
  }

  /**
   * Notifies the ended.
   * @param {Array<Array<*>>} board The board.
   * @param {Number} winner The player who won.
   */
  notifyEnd(board, winner = null) {
    this.socket.emit('end', { board, winner });
  }

  /**
   * Sends an error.
   * @param {Error} error The error.
   */
  notifyError(error) {
    // Named event as 'problem' because the socket already has en event called 'error'
    this.socket.emit('problem', { error });
  }

  /**
   * Sends a close connection message due to an error or disconnection of some player.
   * @param {Array<Array<*>>} board The board.
   * @param {Number} id The player who won.
   */
  notifyClose(id) {
    try {
      this.socket.emit('close', { player: id });
    } catch (error) {
      // Probably the player who had the error or disconnection
      // Do nothing
    }
  }

  /**
   * Register a listener for movement messages.
   * @param {Function} callback The listener.
   */
  listen(callback) {
    this.socket.on('movement', callback);
  }

  /**
   * Register a listener for socket error or disconnection.
   * @param {Function} callback The listener.
   */
  listenDisconnection(callback) {
    const handle = (message) => {
      log(this.socket.id, this.name, message);
      this.disconnected = true;
      callback(this);
    };

    this.socket.on('disconnect', handle);
    this.socket.on('error', handle);
  }
}
