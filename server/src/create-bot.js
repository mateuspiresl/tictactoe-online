/* eslint class-methods-use-this: "off", no-unused-vars: "off" */

import io from 'socket.io-client';
import { log as logFull } from './utils';


function getRandomMovement() {
  return Math.floor(Math.random() / (1 / 3));
}

function createRandomName() {
  const aLetters = 'aeiouy';
  const bLetters = 'bcdfgjklmnpqrstvxz';
  const getRandom = letters => letters[parseInt(letters.length * Math.random(), 10)];
  return `${getRandom(bLetters).toUpperCase()}${getRandom(aLetters)}${getRandom(bLetters)}`;
}


export default function createBot(host, waitingPlayerId) {
  const selfName = createRandomName();
  const log = (message, ...args) => logFull('Bot', selfName, message, ...args);
  let selfId = null;

  log('Connecting bot to the server');
  const player = io(host);

  function move(board) {
    for (;;) {
      const line = getRandomMovement();
      const column = getRandomMovement();

      if (line <= 2 && column <= 2 && board[line][column] === null) {
        player.emit('movement', { line, column });
        break;
      }
    }
  }

  player.on('connect', () => {
    log('Event connect');
    player.emit('intro', { name: `Bot ${selfName}`, waitingPlayerId });
  });

  player.on('waiting', () => {
    log('Event waiting');
  });

  player.on('start', ({
    id, opponent, board, turn,
  }) => {
    log('Event start', id, opponent, board, turn);
    selfId = id;
    if (turn === selfId) move(board);
  });

  player.on('state', ({ board, turn }) => {
    log('Event update', board, turn);
    if (turn === selfId) move(board);
  });

  player.on('end', ({ board, winner }) => {
    log('Event end', board, winner);
    player.disconnect();
  });

  player.on('close', () => {
    log('Event close');
    player.disconnect();
  });

  player.on('disconnect', () => {
    log('Event disconnect');
  });
}
