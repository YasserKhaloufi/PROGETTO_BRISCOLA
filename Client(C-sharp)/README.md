# Client-Side Components

# Server Class

The `Server` class is a static class that handles the TCP communication with the server.

# Carta Class

Represents a playing card, it is the same on the client-side.

## Remarks

- The `Carta` class can be serialized and deserialized from XML.
- It contains information about the suit, number, value, and image path of the card.

# XMLserializer Class

It is used to serialize and parse Carta objects and commands sent by the server to and from XML.

# Home Class

Represents the home window of the application, where the user can set its username and change the settings.
It obiously opens before the mainWindow and windowAttesa.

# WindowAttesa Class

Represents the waiting window of the game, it updates a counter each time a new player joins and
allows to start the game when there are 2/4 players.
It opens before the mainWindow.

# MainWindow Class

Represents the main game window of the application, its interface gets updated
following the flow of the game turns and rounds. Here, the user is able to play cards.

# Impostazioni Class

Represents the settings window of the application, it allows to set the IP and port of the server to which the client has to connect for playing.