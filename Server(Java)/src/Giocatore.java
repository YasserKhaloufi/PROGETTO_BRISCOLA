import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Giocatore {
    private Socket connectionSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public Giocatore(Socket connectionSocket) throws IOException {
        // Aspetto connessioni TCP dai client
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        // Creo  il flusso di invio
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
    }
    public Socket getConnectionSocket() {
        return connectionSocket;
    }
    public void setConnectionSocket(Socket socketConnection) {
        this.connectionSocket = socketConnection;
    }
    public BufferedReader getInFromClient() {
        return inFromClient;
    }
    public void setInFromClient(BufferedReader inFromClient) {
        this.inFromClient = inFromClient;
    }
    public DataOutputStream getOutToClient() {
        return outToClient;
    }
    public void setOutToClient(DataOutputStream outToClient) {
        this.outToClient = outToClient;
    }
}
