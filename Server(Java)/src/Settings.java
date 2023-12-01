/**
 * Stores constant values used during the management of the game.
 * <p>
 * Its values are static, so they can be accessed from every class.
 */
public class Settings {
    
    /**
     * The default port number for the game server.
     */
    public static int porta = 777;
    
    /**
     * The minimum number of players required to start a game.
     */
    public static int minGiocatori = 2;
    
    /**
     * The maximum number of players allowed in a game.
     */
    public static int maxGiocatori = 4;
    
    /**
     * The default timeout value for searching a new player. See: {@link server#cercaGiocatori()}
     */
    public static int timeOut = 1000;
}
