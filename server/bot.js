import io from 'socket.io-client';


const player = io('http://localhost:3000/');
let selfId = null;

function getRandomMovement() {
  return Math.floor(Math.random() / (1 / 3));
}

function move(board) {
  while (true) {
    const line = getRandomMovement();
    const column = getRandomMovement();

    if (line <= 2 && column <= 2 && board[line][column] === null) {
      player.emit('movement', { line, column });
      break;
    }
  }
}

function createRandomName() {
  const aLetters = 'aeiouy';
  const bLetters = 'bcdfgjklmnpqrstvxz';
  const getRandom = letters => letters[parseInt(letters.length * Math.random(), 10)];
  return `${getRandom(bLetters).toUpperCase()}${getRandom(aLetters)}${getRandom(bLetters)}`;
}

player.on('connect', () => {
  console.log('event connect');
  player.emit('intro', { name: `Bot ${createRandomName()}` });
});

player.on('waiting', () => {
  console.log('event waiting');
});

player.on('start', ({
  id, opponent, board, turn,
}) => {
  console.log('event start', id, opponent, board, turn);
  selfId = id;
  if (turn === selfId) move(board);
});

player.on('state', ({ board, turn }) => {
  console.log('event update', board, turn);
  if (turn === selfId) move(board);
});

player.on('end', ({ board, winner }) => {
  console.log('event end', board, winner);
  player.disconnect();
});

player.on('close', () => {
  console.log('event close');
  player.disconnect();
});

player.on('disconnect', () => {
  console.log('event disconnect');
});
