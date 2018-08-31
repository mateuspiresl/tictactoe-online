## Messages

### In

- `intro`: `{ name: String }`
  - Introduces the player with his name.
- `movement`: `{ line: Number, column: Number }`
  - Makes a movement to a cell at the line and column.

### Out

- `waiting`: `{ }`
  - Notifies the player is waiting for a match.
- `start`: `{ id: Number, opponent: String, board: Array<Array<*>>, turn: Number }`
  - Gives the initial information and starts the game.
  - The `id` represents the player's ID, which is used to mark the board.
  - The `opponent` is the opponent's name.
  - The `board` is an array of lines of length 3, each one has 3 cells. If a cell is not marked, it is `null`, otherwise it has the player's ID.
  - The `turn` is the player's ID which is in turn.
- `update`: `{ board: Array<Array<*>>, turn: Number }`
  - Gives the current state of the board and the current turn.
- `winner`: `{ id: Number, board: Array<Array<*>> }`
  - The game ended and the winner is the `id` value. The current state of the board is also given.
- `problem`: `{ error: Error }`
  - An error happened.
- `close`: `{ id: Number }`
  - One of the players disconnect, the one which has the `id`, so the server is closing the game.
