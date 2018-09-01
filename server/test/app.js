/* globals describe, after, before, it, afterEach, beforeEach */

import io from 'socket.io-client';
import { expect } from 'chai';
import app from '../src/app';


describe('App', () => {
  let server = null;

  before(async () => {
    server = await app();
  });

  after(() => {
    server.close();
  });

  describe('Waiting', () => {
    it('the first player receives waiting', (done) => {
      const playerA = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerA.on('waiting', () => {
        playerA.disconnect();
        done();
      });
    });

    it('the second player does not receive waiting', (done) => {
      const playerA = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerA.on('waiting', () => {
        const playerB = io('http://localhost:3000/');

        playerB.on('waiting', () => done('The second player received a waiting message'));
        playerB.emit('intro', { name: 'Player B' });

        setTimeout(() => {
          playerA.disconnect();
          playerB.disconnect();
          done();
        }, 800);
      });
    });
  });

  describe('Start', () => {
    let playerA;
    let playerB;

    beforeEach(() => {
      playerA = io('http://localhost:3000/');
      playerB = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerB.emit('intro', { name: 'Player B' });
    });

    afterEach(() => {
      playerA.close();
      playerB.close();
    });

    it('start of the first player', (done) => {
      playerA.on('start', ({
        id, opponent, board, turn,
      }) => {
        try {
          expect(id).to.equal(0);
          expect(opponent).to.equal('Player B');
          expect(board).to.have.deep.members([
            [null, null, null],
            [null, null, null],
            [null, null, null],
          ]);
          expect(turn).to.equal(0);
          done();
        } catch (error) {
          done(error);
        }
      });
    });

    it('start of the second player', (done) => {
      playerB.on('start', ({
        id, opponent, board, turn,
      }) => {
        try {
          expect(id).to.equal(1);
          expect(opponent).to.equal('Player A');
          expect(board).to.have.deep.members([
            [null, null, null],
            [null, null, null],
            [null, null, null],
          ]);
          expect(turn).to.equal(0);
          done();
        } catch (error) {
          done(error);
        }
      });
    });
  });

  describe('Movements', () => {
    let playerA;
    let playerB;

    beforeEach((done) => {
      playerA = io('http://localhost:3000/');
      playerB = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerB.emit('intro', { name: 'Player B' });

      playerA.on('start', () => done());
      playerA.on('end', () => done('There can\'t have a winner at this moment'));
    });

    afterEach(() => {
      playerA.close();
      playerB.close();
    });

    it('first movement update to the first player', (done) => {
      playerA.emit('movement', { line: 0, column: 0 });
      playerA.on('state', ({ board, turn }) => {
        try {
          expect(board).to.have.deep.members([
            [0, null, null],
            [null, null, null],
            [null, null, null],
          ]);
          expect(turn).to.equal(1);
          done();
        } catch (error) {
          done(error);
        }
      });
    });

    it('first movement update to the second player', (done) => {
      playerA.emit('movement', { line: 0, column: 0 });
      playerB.on('state', ({ board, turn }) => {
        try {
          expect(board).to.have.deep.members([
            [0, null, null],
            [null, null, null],
            [null, null, null],
          ]);
          expect(turn).to.equal(1);
          done();
        } catch (error) {
          done(error);
        }
      });
    });

    it('second movement update', (done) => {
      playerA.emit('movement', { line: 0, column: 0 });
      playerB.emit('movement', { line: 1, column: 1 });
      playerA.on('state', ({ board, turn }) => {
        if (turn === 0) {
          try {
            expect(board).to.have.deep.members([
              [0, null, null],
              [null, 1, null],
              [null, null, null],
            ]);
            done();
          } catch (error) {
            done(error);
          }
        }
      });
    });
  });

  describe('Ending', () => {
    let playerA;
    let playerB;

    beforeEach((done) => {
      playerA = io('http://localhost:3000/');
      playerB = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerB.emit('intro', { name: 'Player B' });

      playerA.on('start', () => done());
    });

    afterEach(() => {
      playerA.close();
      playerB.close();
    });

    it('first player winning', (done) => {
      const movements = [
        () => playerA.emit('movement', { line: 0, column: 0 }),
        () => playerB.emit('movement', { line: 0, column: 2 }),
        () => playerA.emit('movement', { line: 1, column: 0 }),
        () => playerB.emit('movement', { line: 1, column: 2 }),
        () => playerA.emit('movement', { line: 2, column: 0 }),
      ];

      playerA.on('state', () => movements.splice(0, 1)[0]());
      playerA.on('end', ({ board, winner }) => {
        expect(board).to.have.deep.members([
          [0, null, 1],
          [0, null, 1],
          [0, null, null],
        ]);
        expect(winner).to.equal(0);
        done();
      });

      movements.splice(0, 1)[0]();
    });

    it('no winner', (done) => {
      const movements = [
        () => playerA.emit('movement', { line: 0, column: 0 }),
        () => playerB.emit('movement', { line: 0, column: 1 }),
        () => playerA.emit('movement', { line: 1, column: 0 }),
        () => playerB.emit('movement', { line: 1, column: 1 }),
        () => playerA.emit('movement', { line: 2, column: 1 }),
        () => playerB.emit('movement', { line: 2, column: 0 }),
        () => playerA.emit('movement', { line: 0, column: 2 }),
        () => playerB.emit('movement', { line: 1, column: 2 }),
        () => playerA.emit('movement', { line: 2, column: 2 }),
      ];

      const play = () => movements.splice(0, 1)[0]();

      playerA.on('state', play);
      playerA.on('end', ({ winner }) => {
        expect(winner).to.equal(null);
        done();
      });

      play();
    });
  });

  describe('Disconnection', () => {
    it('disconnect the waiting player and start a game with the two next players', (done) => {
      const playerA = io('http://localhost:3000/');
      const playerB = io('http://localhost:3000/');
      const playerC = io('http://localhost:3000/');

      playerA.emit('intro', { name: 'Player A' });
      playerA.on('waiting', () => {
        playerA.disconnect();

        playerB.emit('intro', { name: 'Player B' });
        playerC.emit('intro', { name: 'Player C' });
      });

      playerB.on('start', ({ id, opponent }) => {
        try {
          expect(id).to.equal(0);
          expect(opponent).to.equal('Player C');

          playerB.disconnect();
          playerC.disconnect();
          done();
        } catch (error) {
          done(error);
        }
      });
    });
  });
});
