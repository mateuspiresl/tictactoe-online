/* eslint import/prefer-default-export: "off", no-console: "off", no-unused-vars: "off" */

const RESET = '\x1b[0m';
const BRIGHT = '\x1b[1m';
const DIM = '\x1b[2m';
const UNDERSCORE = '\x1b[4m';
const REVERSE = '\x1b[7m';

const format = `${DIM}(%s)${RESET} ${BRIGHT}%s${RESET}: %s`;


/**
 * Logs.
 * @param {String} id The socket id.
 * @param {String} name The player's name.
 * @param {String} message The message.
 * @param {*} args (Optional) arguments to append.
 */
export const log = (id, name, message, ...args) => (
  console.log(format, id.substr(0, 6), name, message, ...args)
);
