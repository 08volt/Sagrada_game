package progettoIngSW.Network.Client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import progettoIngSW.CommandInterface;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;
import progettoIngSW.ViewInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.*;


/**
 * classe di comunicazione lato client con il server tramite una connessione di tipo Socket.
 * implementa ClientToServer per invocare sul server le funzionalità di base: login e caricamento di un nuovo pattern.
 * implementa CommandInterface per inviare al server i comandi dell'utente
 * implemnta Runnable per stare in attesa di update da parte del server e trasferirli alla view
 *
 * la comunicazione con il ServerSocket avviene tramite scrittura e lettura di JsonObject
 */

public class ClientSocket implements ClientToServer, Runnable, CommandInterface {

    //INVIA LE RICHIESTE AL SERVERSOCKET E LEGGE I MESSAGGI CHE ARRIVANO

    private ViewInterface viewInterface;
    private Socket socket;
    private ClientSocketHelperThread ask;
    private Client client;


    private BufferedReader inputStream;
    private PrintWriter outputStream;
    private final Object monitor = new Object();

    private Gson gason = new Gson();

    public final static List<JsonObject> requests = Collections.synchronizedList(new LinkedList());

    public ClientSocket(String serverAddress, ViewInterface viewInterface, Client client) throws IOException {

        this.viewInterface = viewInterface;
        socket = new Socket(serverAddress, 8000);
        this.client = client;

        this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputStream = new PrintWriter(socket.getOutputStream(),true);
        this.ask = new ClientSocketHelperThread(viewInterface,outputStream);

    }




    /**
     * Formato richiesta Json:
     *
     * "code" : tipo di richiesta: login
     * "name" : username dell'utente
     * "password" : password dell'utente
     *
     * Formato risposta Json:
     *
     * "code":
     *      - "logged" setta logged a true nel client
     *      - "notlogged" setta logged a false nel client
     *
     * @throws NameNotAvailableException   code: "name"
     * @throws FullLobbyException    code: "lobby"
     * @throws WrongPasswordException  code: "password"
     */
    @Override
    public void login() throws NameNotAvailableException, FullLobbyException, WrongPasswordException {


        JsonObject jason = new JsonObject();
        jason.addProperty("code","login");
        jason.addProperty("name", client.getName());
        jason.addProperty("password", client.getPassword());
        outputStream.println(jason);


        JsonObject risp = null;

        while (requests.isEmpty()) {


        }
        risp = requests.remove(0);


        if(risp.get("code").getAsString().equals("logged")){
            client.setLogged(true);
        }else if(risp.get("code").getAsString().equals("name")) {
            throw new NameNotAvailableException();
        }else if(risp.get("code").getAsString().equals("lobby")){
            throw new FullLobbyException();
        }else if(risp.get("code").getAsString().equals("notlogged")){
            client.setLogged(false);
        }
        else if(risp.get("code").getAsString().equals("password")){
            throw new WrongPasswordException();
        }


    }


    /**
     * Formato richiesta Json:
     *
     * "code" : tipo di richiesta: placedice
     * "draft" : posizione nel draft del dado da piazzare
     * "window" : posizione della windows frame dove piazzare il dado
     *
     * Formato risposta Json: "code" : tipo eccezzione
     *
     * @param posDraft posizione nel draft del dado da piazzare
     * @param posWindow posizione nella windowsframe dove l'utente desidera piazzare il dado
     * @throws CellNotEmptyException code:  "cellnotempty"
     * @throws NotPlayingException  code:  "notplaying"
     * @throws DiceNotFoundException  code:  "dicenotfound"
     * @throws RulesBreakException  code:  "rules"
     */

    @Override
    public void placeDice(int posDraft, int posWindow) throws CellNotEmptyException, NotPlayingException, DiceNotFoundException, RulesBreakException {

        JsonObject jason = new JsonObject();
        jason.addProperty("code","placedice");
        jason.addProperty("draft",posDraft);
        jason.addProperty("window",posWindow);
        outputStream.println(jason);

        JsonObject risp = null;


        while (requests.isEmpty()) {


        }
        risp = requests.remove(0);

        if(risp.get("code").getAsString().equals("cellnotempty")){
            throw new CellNotEmptyException();

        }else if(risp.get("code").getAsString().equals("notplaying")){
            throw new NotPlayingException(null);

        }else if(risp.get("code").getAsString().equals("dicenotfound")){
            throw new DiceNotFoundException();

        }else if(risp.get("code").getAsString().equals("rules")){
            RulesBreakException.CODE code = gason.fromJson( risp.get("type").getAsString(),RulesBreakException.CODE.class);

            throw new RulesBreakException(code);

        }

    }


    /**
     *
     * Formato richiesta Json:
     *
     * "code" : tipo di richiesta: tool
     * "pos" : posizione della tool card che si vuole usare nell'array di game
     *
     * Formato risposta Json:
     *
     * "code": tipo di eccezzione
     *
     * @param toolNumber posizione della tool card che si vuole usare nell'array di game
     * @throws DiceNotFoundException code:  "dicenotfound"
     * @throws CellNotEmptyException code:  "cellnotempty"
     * @throws RulesBreakException code:  "rules"
     * @throws InvalidParamsException code: "invelid params"
     * @throws NotPlayingException code:  "notplaying"
     * @throws NotValidCellException code: "notvalidcell"
     */
    @Override
    public void useToolCard(int toolNumber) throws DiceNotFoundException, CellNotEmptyException, RulesBreakException, InvalidParamsException, NotPlayingException, NotValidCellException {

        JsonObject jason = new JsonObject();
        jason.addProperty("code","tool");
        jason.addProperty("pos",toolNumber);
        outputStream.println(jason);

        JsonObject risp = null;

        while (requests.isEmpty()) {


        }
        risp = requests.remove(0);

        if(risp.get("code").getAsString().equals("cellnotempty")){
            throw new CellNotEmptyException();

        }else if(risp.get("code").getAsString().equals("notplaying")){
            throw new NotPlayingException(null);

        }else if(risp.get("code").getAsString().equals("dicenotfound")){
            throw new DiceNotFoundException();

        }else if(risp.get("code").getAsString().equals("rules")){
            RulesBreakException.CODE code = gason.fromJson( risp.get("type").getAsString(),RulesBreakException.CODE.class);
            throw new RulesBreakException(code);

        }else if(risp.get("code").getAsString().equals("invalidparams")){
            throw new InvalidParamsException(null);

        }else if(risp.get("code").getAsString().equals("notvalidcell")){
            throw new NotValidCellException();

        }

    }




    /**
     * Formato richiesta Json:
     *
     * "code" : tipo di richiesta: endTurn
     */
    @Override
    public void endTurn() {
        client.endTurn();
        JsonObject jason = new JsonObject();
        jason.addProperty("code","endTurn");
        outputStream.println(jason);

    }



    /**
     * Formato richiesta Json:
     *
     * "colors" : array delle restrizioni di colore
     * "numbers" : array delle restrizioni di numero
     * "name" : nome del pattern
     * "difficult" : difficoltà del pattern
     *
     * @param pattern pattern da inviare
     */

    @Override
    public void uploadPattern(Pattern pattern) {
        JsonObject jason = new JsonObject();
        Gson gason = new Gson();

        jason.addProperty("code","NewPattern");
        jason.addProperty("colors", gason.toJson(pattern.getColors()));
        jason.addProperty("numbers", gason.toJson(pattern.getNumbers()));
        jason.addProperty("name",pattern.getName());
        jason.addProperty("difficult",pattern.getDifficult());



        outputStream.println(jason);
    }


    /**
     *  lettura delle richieste del server e degli update
     *
     *  tipi di Json "code" : Metodo corrispondente in RMI
     *
     *      - "endGame" : endGame( Json.get("rank") )
     *      - "increase" : askIncrease()
     *      - "windowpos" : askWindowPos()
     *      - "disconnection" : alertDisconnection(Json.get("name"))
     *      - "connection" : alertConnection(Json.get("name"))
     *      - "trackpos" : askTrackPos()
     *      - "number" : askNumber( Json.get("color"))
     *      - "howmany" : askHowMany()
     *      - "patterns" : askWhichPattern(Json.get("array"))
     *      - "draftpos" : askDraftPos()
     *      - "startTurn" : startTurn()
     *      - "singleDice" : printDice(Json.get("dice"))
     *      - "timer" : endTimer()
     *      - "update" : update relativo a Json.get("what")
     */

    @Override
    public void run() {
        ask.start();

        while (true) {
            try {

                JsonParser jp = new JsonParser();

                String input = inputStream.readLine();
                JsonObject request = jp.parse(input).getAsJsonObject();

                if (request.get("code").getAsString().equals("endGame")) {
                    Type i = new TypeToken<ArrayList<Integer>>() {}.getType();
                    viewInterface.endGame(gason.fromJson(request.get("rank").getAsString(),i));
                    break;

                } else if (request.get("code").getAsString().equals("increase")) {
                    ClientSocketHelperThread.commands.add(request);

                } else if (request.get("code").getAsString().equals("windowpos")) {
                    ClientSocketHelperThread.commands.add(request);

                }else if (request.get("code").getAsString().equals("disconnection")) {
                    viewInterface.alertDisconnectionOf(request.get("name").getAsString());

                } else if (request.get("code").getAsString().equals("connection")) {
                    if(!request.get("name").getAsString().equals(getName()))
                        viewInterface.alertConnectionOf(request.get("name").getAsString());

                } else if (request.get("code").getAsString().equals("trackpos")) {
                    ClientSocketHelperThread.commands.add(request);

                } else if (request.get("code").getAsString().equals("number")) {
                    ClientSocketHelperThread.commands.add(request);
                } else if (request.get("code").getAsString().equals("howmany")) {
                    ClientSocketHelperThread.commands.add(request);
                } else if (request.get("code").getAsString().equals("patterns")) {
                    ClientSocketHelperThread.commands.add(request);

                } else if (request.get("code").getAsString().equals("draftpos")) {
                    ClientSocketHelperThread.commands.add(request);

                } else if (request.get("code").getAsString().equals("startTurn")) {
                    client.startTurn();
                }  else if (request.get("code").getAsString().equals("singleDice")) {
                    viewInterface.printDice(gason.fromJson(request.get("dice").getAsString(),Dice.class));
                } else if (request.get("code").getAsString().equals("timer")) {
                    viewInterface.endTimer();
                } else if (request.get("code").getAsString().equals("update")) {

                    String what = request.get("what").getAsString();
                    if (what.equals("currentPlayer")) { //current player
                        viewInterface.updateCurrentPlayer(request.get("current").getAsInt());
                    } else if (what.equals("playerList")) {

                        Player p = new Player(request.get("name").getAsString(),request.get("pass").getAsString());
                        p.setNumTokens(request.get("numTok").getAsInt());
                        p.setScore(request.get("score").getAsInt());
                        p.setMoves(gason.fromJson(request.get("moves").getAsString(),Moves.class));
                        p.setCurrentTurn(gason.fromJson(request.get("currentT").getAsString(),Turns.class));
                        p.setPrivateObjectiveCard(gason.fromJson(request.get("privoc").getAsString(),PrivateObjectiveCard.class));

                        viewInterface.updatePlayerList(p,request.get("pos").getAsInt());

                    }else if(what.equals("tools")){

                        Type i = new TypeToken<ArrayList<Integer>>() {
                        }.getType();
                        viewInterface.updateTools(gason.fromJson(request.get("tools").getAsString(),i));

                    } else if (what.equals("gameTurn")) { //current game turn
                        viewInterface.updateGameTurn(gason.fromJson(request.get("turn").getAsString(), Turns.class));

                    }else if (what.equals("round")) { //current game turn
                        viewInterface.updateCurrentRound(request.get("round").getAsInt());

                    }else if (what.equals("poc")) {
                        Type i = new TypeToken<ArrayList<Integer>>() {
                        }.getType();
                        viewInterface.updatePublicObjectiveCards(gason.fromJson(request.get("poc").getAsString(), i));


                    } else if (what.equals("window")) {
                        WindowFrame w = new WindowFrame(gason.fromJson(request.get("window").getAsString(), Cell[].class));

                        w.setRow(request.get("row").getAsInt());
                        w.setCol(request.get("col").getAsInt());
                        viewInterface.updateWindowsFrame(w);


                    } else if (what.equals("roundtrack")) {
                        Type t = new TypeToken<HashMap<Integer, ArrayList<Dice>>>() {
                        }.getType();
                        viewInterface.updateRoundTrack(gason.fromJson(request.get("roundtrack").getAsString(), t));

                    } else if (what.equals("tokens")) {

                        Type t = new TypeToken<ArrayList<Integer>>() {
                        }.getType();

                        viewInterface.updateTokens(gason.fromJson(request.get("tokens").getAsString(), t));


                    } else if (what.equals("draft")) {
                        Type t = new TypeToken<ArrayList<Dice>>() {
                        }.getType();

                        viewInterface.updateDraftPool(gason.fromJson(request.get("draft").getAsString(), t));


                    }else if (what.equals("wf")) {
                        WindowFrame wf = new WindowFrame(gason.fromJson(request.get("wf").getAsString(), Cell[].class));
                        wf.setRow(request.get("row").getAsInt());
                        wf.setCol(request.get("col").getAsInt());
                        viewInterface.updateWf(wf,request.get("pos").getAsInt());


                    }
                }else{

                    synchronized (monitor) {
                        requests.add(request);
                        monitor.notifyAll();
                    }

                }
            }catch (IOException | NullPointerException e){
                viewInterface.connectionError();
                break;
            }

        }

        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException ignored) {

        }


        try {
            socket.close();
        } catch (IOException ignored) {

        }


    }

    @Override
    public String getName() {
        return client.getName();
    }

}
