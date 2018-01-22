
# Message protocol

## Types referenced

    PlayerID : @id
    MessageID : %id
    GameTerms : {
        size : Int
        komi : Float
        handicap : Int(0-9)
    }


## Beginning a game

### `igo-request-match`

Message for requesting a match, which all friends will be able to see

* gameTerms : GameTerms


### `igo-offer-match`

Message for initiating a match with a particular uxer

* opponent : PlayerID
* myColor : Black | White
* gameTerms : GameTerms


### `igo-accept-match`

Game on.

* message : MessageID
    - The `igo-offer-match` message to accept


### `igo-decline-match`

Used to decline a match suggested by another player. Dead-ends the message chain.

* message: MessageID
    - The `igo-offer-match` message to decline


## During the game

### `igo-move`

* position: BoardCoords | Pass | Resign
* prevMove : MessageID
    - the previous `igo-move` message, or if this is the first move, the `igo-offer-match` message which started the game


### `igo-chat`

Say something during a game, whether your own or someone else's

* message : String
    - what you want to say
* move : MessageID
    - what move was showing when you said it


### `igo-observe-move`

Shows that you were watching a game at a particular move, to let others know you are there. Typically a client would send this message upon first visiting a game, and then again for every new move that comes in while still observing the game. Other clients can update the observer list by simply checking how many of these (unique per uxer) messages there are for the current move.

* move : MessageID


## Ending the game

### `igo-post-results`

???
