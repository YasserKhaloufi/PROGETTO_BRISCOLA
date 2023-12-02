# Client-Side Components

# Server Class

The `Server` class is a static class that handles the server-side operations of the application.

## Properties

- `IP`: A string that holds the IP address of the server. The default value is "127.0.0.1".
- `PORT`: An integer that holds the port number of the server. The default value is 777.

## Methods

- `handShake(string indirizzoIP, int numPorta)`: This method is used to connect to a different server other than the default. It takes the IP address and port number as parameters.
- `InviaCarta(Carta c)`: This method is used to send a specific command. The details of this command are not provided in the excerpt.

## Private Fields

- `client`: A `TcpClient` object that is used to connect to the server.
- `stream`: A `NetworkStream` object that is used to send and receive data from the server.

# Carta Class

The `Carta` class represents a playing card.

## Properties

- `seme`: A string that represents the suit of the playing card.
- `numero`: A char that represents the number of the card.

## Remarks

- The `Carta` class can be serialized and deserialized from XML.
- It contains information about the suit, number, value, and image path of the card.

## Events

- `PropertyChanged`: An event that is triggered when a property changes. This is part of the `INotifyPropertyChanged` interface.

# XMLserializer Class

The `XMLserializer` class is used to serialize and deserialize objects to and from XML.

## Methods

- `SaveLista(string filePath, List<Carta> lista)`: This method is used to save a list of `Carta` objects to an XML file. It takes a file path and a list of `Carta` objects as parameters. The XML document is indented for readability.

- `SerializzaLista(List<Carta> lista)`: This is a private method used to serialize a list of `Carta` objects into an `XmlDocument`. It takes a list of `Carta` objects as a parameter.

## Remarks

- The `XMLserializer` class is mainly used to automatically generate the deck.

# Home Class

The `Home` class represents the home window of the application.

## Properties

- `isClosingFromButton`: A boolean that checks if the window is closing from the "X" button or not.
- `username`: A string that holds the username of the current user.

## Methods

- `Home()`: This is the constructor of the `Home` class. It initializes the components, sets the closing event, sets the background color, and sets the default text of `txtNome`.

- `btnStart_Click(object sender, RoutedEventArgs e)`: This method is triggered when the start button is clicked. It checks if the text of `txtNome` is not empty and not equal to "Inserisci nome:".

## Remarks

- The `Home` window opens before the main window.
- If the window is closed using the "X" button at the top right, the application closes.

# WindowAttesa Class

The `WindowAttesa` class represents the waiting window of the application.
## Properties

- `isClosingFromStartButton`: A boolean that checks if the window is closing from the "Start" button or not.
- `attendi`: A boolean that checks if the window is waiting for other players or not.

## Methods

- `WindowAttesa()`: This is the constructor of the `WindowAttesa` class. It initializes the components, sets the closing event, and disables the start game button. It also starts a task to search for other players.

## Remarks

- The `WindowAttesa` window opens after the `Home` window.
- If the window is closed using the "X" button at the top right, the application closes.

# MainWindow Class

The `MainWindow` class represents the main game window of the application.

## Properties

- `username`: A string that holds the username of the current player.
- `buttons`: A list of `Button` objects that are generated dynamically.
- `briscola`: A `Carta` object that represents the trump card in the game.
- `mano`: An `ObservableCollection` of `Carta` objects that represents the player's hand.

## Remarks

- The `MainWindow` is where the main gameplay happens.
- The `MainWindow` opens after the `WindowAttesa` window.

# Impostazioni Class

The `Impostazioni` class represents the settings window of the application.

## Properties

- `ipAndPort`: A list of strings that holds the IP address and port number.

## Methods

- `Impostazioni()`: This is the constructor of the `Impostazioni` class. It initializes the components of the window.

- `btnConferma_Click(object sender, RoutedEventArgs e)`: This method is triggered when the confirm button is clicked. It initializes the `ipAndPort` list and gets the IP address and port number from the text boxes.

## Remarks

- The `Impostazioni` window is used to change the server settings.