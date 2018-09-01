import io from 'socket.io';
import http from 'http';
import Player from './player';
import Game from './game';
import createBot from './create-bot';
import { log } from './utils';


/**
 * Starts the server.
 * @returns {Promise<Server>} The http server.
 */
export default () => {
  const httpServer = http.createServer((req, res) => res.end('Reached.'));
  const ioServer = io(httpServer);
  const port = process.env.PORT || 3000;

  // Holds a player waiting for a match
  let waiting = null;
  let waitingTimeoutEvent = null;

  // If there is no other connection, put this player in waiting
  // But if there is (#waiting has a player), starts a game with both
  function introducePlayer(socket, name) {
    if (waiting) {
      log(socket.id, name, 'Creating game');

      try {
        // Creates the new game and starts
        new Game(waiting, new Player(socket, name)).start();

        // Removes the waiting player to avoid using him with the next connection
        waiting = null;

        // Removes the event used to create a bot
        if (waitingTimeoutEvent) {
          clearTimeout(waitingTimeoutEvent);
        }
      } catch (error) {
        log(socket.id, name, 'Error:', error);

        // Possibly, the client on waiting has gone
        // If it did, the calling to introduce again will put the current player in waiting
        setTimeout(() => introducePlayer(socket, name), 100);
      }
    } else {
      log(socket.id, name, 'Waiting');

      // There is no player waiting, so put the current player
      waiting = new Player(socket, name);

      // Register an event to create a bot if the player waits for more than 10 seconds
      waitingTimeoutEvent = setTimeout(() => {
        log(socket.id, name, 'Timeout, creating bot');
        createBot(`http://localhost:${port}`, socket.id);
      }, 10000);

      // If there's a disconnection, remove from waiting
      waiting.listenDisconnection((player) => {
        log(player.socket.id, player.name, 'Disconnected');

        if (waiting && waiting.socket === socket) {
          waiting = null;
        }
      });

      // Notify the player is waiting for a match
      waiting.notifyWaiting();
    }
  }

  // Call #introducePlayer on every player that introduces himself
  ioServer.on('connection', (socket) => {
    log(socket.id, null, 'New connection');

    socket.emit('connected', 'Send your name to \'intro\'.');
    socket.once('intro', ({ name, waitingPlayerId }) => {
      log(socket.id, name, 'Intro');

      if (waitingPlayerId) {
        if (waiting && waiting.socket.id === waitingPlayerId) {
          introducePlayer(socket, name);
        } else {
          socket.disconnect();
        }
      } else {
        introducePlayer(socket, name);
      }
    });
  });

  // Starts the server
  return new Promise((resolve, reject) => {
    httpServer.listen(port, (error) => {
      if (error) {
        reject(error);
      } else {
        console.log('Listening on port', port);
        resolve(httpServer);
      }
    });
  });
};
