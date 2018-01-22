
# Flume index structure

- games : Map GameID Game
- myGames : List GameID
- ??

# Types

GameID : MessageID

Game : {
  moves : List Move
  chats : List Chat
}

Move : (Int, Int)

Chat : (String, DateTime, PersonID)
