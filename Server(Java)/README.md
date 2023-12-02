# Server-Side Components

## Server Class

The `Server` class is the main component of the server-side application. It waits for players to connect and starts the game when requested.

### Key Features

- Provides various methods for communication with the clients.
- Uses TCP sockets for communication with the clients.
- Acts as a shared class among the different threads during the game phase.

### Key Variables

- `giocatori`: A list of client handlers representing connected players. This list is shared with `threadPartita`.
- `gameStarted`: A boolean value indicating whether the game has started or not. It is shared among the client handlers.
- `endGame`: A boolean value indicating whether the game has ended or not. It is shared among the client handlers.

### Main Method

The main method is the entry point of the server application.

## Carta Class

The `Carta` class represents a playing card in the game.

### Key Features

- Contains information about the suit, number, value, and image path of the card.
- Can be serialized and deserialized from XML.

### Key Variables

- `seme`: The suit of a playing card.
- `numero`: The number of the card.

## XMLserializer Class

The `XMLserializer` class provides methods to serialize and parse `Carta` objects and messages received from clients.

### Key Features

- Converts `Carta` objects into XML Documents and vice versa.
- Extracts the command, the argument, and the `Carta` object from the message.
- Used by the `Carta` class to serialize and parse `Carta` objects.
- Used by the `clientHandler` class to parse the messages received from the clients.

## clientHandler Class

The `clientHandler` class handles the communication with a client.

### Key Features

- Manages the client's socket connection, input and output streams and eventually closes them.
- Depending on the command received from the client, it performs different actions.

### Key Variables

- `connectionSocket`: The socket representing the connection between the client and the server.

## threadPartita Class

The `threadPartita` class is a thread for managing the game.

### Key Features

- Handles the game elements, such as the deck of cards, played cards buffer, and the briscola.
- Responsible for distributing cards to players, managing turns, determining the winner of each round, updating the score of each player and finally determining the winner of the game when the deck is empty.

### Key Variables

- `mazzo`: The list of cards in the deck.
- `carteGiocate`: The list of cards played in a round (since the players can be 2 or 4, the list can contain 2 or 4 cards).
- `briscola`: The briscola card.

## Settings Class

The `Settings` class stores constant values used during the management of the game.

### Key Features

- Its values are static, so they can be accessed from every class.

### Key Variables

- `porta`: The default port number for the game server.
- `minGiocatori`: The minimum number of players required to start a game.
- `maxGiocatori`: The maximum number of players allowed in a game.
- `timeOut`: The default timeout value for searching a new player.