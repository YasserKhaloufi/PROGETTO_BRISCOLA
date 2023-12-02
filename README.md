# PROGETTO_BRISCOLA

PROGETTO_BRISCOLA is a client-server card game called Briscola. The server is written in Java and the client in C#, and they communicate with each other over TCP.

## Game Rules

The game is played with a deck of 40 cards with the values: ace, 2, 3, 4, 5, 6, 7, jack, queen, king, in Italian or French suits. The ranking of the cards in decreasing order is: ace, 3, king (or 10), queen (or 9), jack (or 8), 7, 6, 5, 4, and 2.

### Installing

You'll need .NET framework and Java installed, after that you can just clone or download the repo and execute both the client and the server.

## Built With

* [Java](https://www.java.com) - The server side language
* [C#](https://www.microsoft.com/net) - The client side language

## Project Structure

This project is structured into three main directories:

### Client (C#)

This directory contains the solution that implements the client. It includes:

* WPF interface XAML modules and their relative .cs files.
* The resources necessary for the client to work.

### Server (Java)

This directory contains the source code for the server, consisting of all its modules.

### docs

This directory contains documentation relative to the project. It includes:

* Javadoc documentation for the server.
* XML documentation for the client.
* UML diagrams.

## Authors

* **Yasser Khaloufi**
* **Riccardo De Lorenzo**