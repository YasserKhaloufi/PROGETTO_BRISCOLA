import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Giocatore {

    // Salvo le seguenti informazioni per ogni client, in modo tale da poter rilasciare le risorse impiegate quando server
    private Socket connectionSocket;
    public BufferedReader inFromClient;
    public DataOutputStream outToClient;

    private String username;

    public Giocatore(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }

    public void abbattiConnessione() throws IOException {
        connectionSocket.close();
        inFromClient.close();
        outToClient.close();
    }

    // Get e set vari
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
