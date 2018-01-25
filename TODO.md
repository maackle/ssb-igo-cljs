
* figure out lein deps w/ lumo
* set up basic message schema for game creating and playing
* build flume index
  - map of all Games for all players
    - status: active / ended
    - final score, if ended
    - all moves (invalid or not)
    - all chats
    - whose turn?
* script to create game messages

* build simple front end
  - just get console.log of something from ssb_igo.core
  - then get state to change with flume index
  - then set up routing with:
    - game list view
      - open games
      - active games
      - my games
    - game view - CHAT ONLY
    - view match requests
    - request a match
    - offer a match

* add actual board to game view
  - observing a game, see the board
  - playing a game, make moves that get validated and send messages

* add endgame messages to protocol
* add endgame functionality to game view
  - detect end of game after two consecutive passes
  - mark dead stones
  - compare score calculations
  - continue playing to resolve dispute
