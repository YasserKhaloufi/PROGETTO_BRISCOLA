# Project Overview

This section of project, as you can tell by its name, implements multiplayer game server, designed to handle multiple clients and manage game sessions. It uses TCP sockets for communication and XML for data serialization. The server is composed of several key components, each with its specific role in the application. The following sections provide a detailed overview of these server-side components.

# Server-Side Components

## Server Class

The `Server` class is the main component of the server-side application. It allows players to connect and starts the game when requested.

### Key Features

- Provides various methods for TCP communication with the clients.
- Acts as a shared class among the different threads during the game phase.

### Main Method

The main method is the entry point of the server application.

## Carta Class

The `Carta` class represents a playing card in the game.

### Key Features

- Contains information about the suit, number, value, and image path of the card.
- Can be serialized and deserialized from XML.

## XMLserializer Class

The `XMLserializer` class provides methods to serialize and parse `Carta` objects and messages received from clients.

### Key Features

- Converts `Carta` objects into XML Documents and vice versa.
- Extracts the command, the argument, and the `Carta` object from the message.

## clientHandler Class

The `clientHandler` class mantains and handles the communication with a single client.

### Key Features

- Manages the client's socket connection, input and output streams and eventually closes them.
- Depending on the command received from the client, it performs different actions.

## threadPartita Class

Once the game is started (2 or 4 players are necessary) this thread manages it.

### Key Features

- Handles the game elements, such as the deck, played cards buffer, etc.
- Responsible for distributing cards to players, managing turns, determining the winner of each round, updating the score of each player and finally determining the winner.

## Settings Class

The `Settings` class stores constant values used all around the project.

### Key Features

- Its values are static, so they can be accessed from every class.