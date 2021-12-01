package progettoIngSW.Network.Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import progettoIngSW.Exceptions.*;
import progettoIngSW.Model.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;



/**
 * Thread di comunicazione lato server con ogni clint tramite una connessione di tipo Socket.
 *
 * la comunicazione con il ClientSocket avviene tramite scrittura e lettura di JsonObject
 */

public class ServerSocketHandler extends Thread {
    private Socket socket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private Server server;
    private String username = "";
    private boolean stop = false;
    Gson gason = new Gson();

    private ServerSocketHelperThread toolExcecuter;


    List<JsonObject> requests = Collections.synchronizedList(new LinkedList<>());

    public String getUsername() {
        return username;
    }




    public ServerSocketHandler(Socket socket, Server s) throws IOException {
        this.socket = socket;
        this.server = s;

        this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputStream = new PrintWriter(socket.getOutputStream(),true);


    }


    @Override
    public void interrupt() {
        this.stop = true;
        super.interrupt();
    }

    /**
     * legge le azioni che il client vuole compiere e le invia al server centrale.
     */

    public void run() {


        do try {

            JsonParser jp = new JsonParser();
            JsonObject request = jp.parse(inputStream.readLine()).getAsJsonObject();
            JsonObject response = new JsonObject();
            if (request.get("code").getAsString().equals("login")) {
                this.username = request.get("name").getAsString();
                String password = request.get("password").getAsString();
                response.addProperty("what", "login");

                try {
                    login(password);
                    response.addProperty("code", "logged");
                    this.toolExcecuter = new ServerSocketHelperThread(server,this);
                    toolExcecuter.start();

                } catch (FullLobbyException e) {
                    this.username = "";
                    response.addProperty("code", "lobby");
                } catch (NameNotAvailableException e) {
                    this.username = "";
                    response.addProperty("code", "name");
                } catch (WrongPasswordException e) {
                    this.username = "";
                    response.addProperty("code", "password");
                }finally {
                    writeOutput(response);

                }

            } else if (request.get("code").getAsString().equals("endTurn")) { //RICHIESTA DI TERMINARE IL TURNO
                endTurn();
            } else if (request.get("code").getAsString().equals("NewPattern")) { //UPLOAD NUOVO PATTERN

                Pattern p = new Pattern();

                p.setNumbers(gason.fromJson(request.get("numbers").getAsString(), int[].class));
                p.setColors(gason.fromJson(request.get("colors").getAsString(), Colors[].class));
                p.setName(request.get("name").getAsString());
                p.setDifficult(request.get("difficult").getAsInt());

                server.uploadPattern(p);


            } else if (request.get("code").getAsString().equals("placedice")) { //RICHIESTA DI PIAZZZARE UN DADO
                int posD = request.get("draft").getAsInt();
                int i = request.get("window").getAsInt();
                response.addProperty("what", "place");
                try {
                    server.placeDice(posD, i, getUsername());
                    response.addProperty("code", "ok");
                } catch (CellNotEmptyException e) {
                    response.addProperty("code", "cellnotempty");


                } catch (NotPlayingException e) {
                    //out.println("NOTPLAYING");
                    response.addProperty("code", "notplaying");

                } catch (DiceNotFoundException e) {
                    //out.println("DICENOTFOUND");
                    response.addProperty("code", "dicenotfound");


                } catch (RulesBreakException e) {
                    //out.println("RULES "+e.getType().name());
                    response.addProperty("code", "rules");
                    response.addProperty("type", gason.toJson(e.getType()));

                }finally {
                    writeOutput(response);
                }
            } else if (request.get("code").getAsString().equals("tool")) {
                toolExcecuter.toolCommands.add(request);
            } else {
                requests.add(request);
            }
        } catch (IOException e) {
            this.stop = true;
            JsonObject error = new JsonObject();
            error.addProperty("code", 4);
            requests.add(error);
            server.clientDisconnection(username);
        } catch (NullPointerException e) {
            JsonObject error = new JsonObject();
            error.addProperty("code", 4);
            requests.add(error);
            this.stop = true;
            server.clientDisconnection(username);

        } while (!stop);

        try {
            socket.close();
        } catch (IOException ignored) {

        }

    }

    private void login(String password) throws FullLobbyException, NameNotAvailableException, WrongPasswordException {
        if(server.login(username, password)) {
            server.askForUpdate();
        }

    }

    public void endTurn() {
        server.endTurn(username);
    }



    public boolean askIncrease() throws EndTimerException, MoveStoppedException {

        JsonObject request = new JsonObject();
        request.addProperty("code","increase");
        writeOutput(request);


        while (requests.isEmpty()) {

        }

        JsonObject response = requests.remove(0);


        if(response.has("exception")){
                if(response.get("exception").getAsString().equals("timer"))
                    throw new EndTimerException();
                else if (response.get("exception").getAsString().equals("move"))
                    throw new MoveStoppedException();
        }

        return response.get("code").getAsBoolean();

    }

    public int askWindowPos(boolean placement) throws EndTimerException, MoveStoppedException {

        JsonObject request = new JsonObject();
        request.addProperty("code","windowpos");
        request.addProperty("placement",placement);
        writeOutput(request);


        while (requests.isEmpty()) {

        }


        JsonObject response = requests.remove(0);


        if(response.has("exception")){
            if(response.get("exception").getAsString().equals("timer"))
                throw new EndTimerException();
            else if (response.get("exception").getAsString().equals("move"))
                throw new MoveStoppedException();
        }

        return response.get("code").getAsInt();

    }

    public int[] askRoundTrackPos() throws EndTimerException, MoveStoppedException {
        JsonObject request = new JsonObject();
        request.addProperty("code","trackpos");
        writeOutput(request);


        while (requests.isEmpty()) {

        }


        JsonObject response = requests.remove(0);


        if(response.has("exception")){
            if(response.get("exception").getAsString().equals("timer"))
                throw new EndTimerException();
            else if (response.get("exception").getAsString().equals("move"))
                throw new MoveStoppedException();
        }


        return gason.fromJson(response.getAsString(),int[].class);
    }

    public int askNumber(Colors col) throws EndTimerException, MoveStoppedException {
        JsonObject request = new JsonObject();
        request.addProperty("code","number");
        request.addProperty("color",gason.toJson(col));
        writeOutput(request);


        while (requests.isEmpty()) {

        }


        JsonObject response = requests.remove(0);


        if(response.has("exception")){
            if(response.get("exception").getAsString().equals("timer"))
                throw new EndTimerException();
            else if (response.get("exception").getAsString().equals("move"))
                throw new MoveStoppedException();
        }
        return response.get("code").getAsInt();

    }

    public boolean askHowMany() throws EndTimerException, MoveStoppedException {
        JsonObject request = new JsonObject();
        request.addProperty("code","howmany");
        writeOutput(request);


        while (requests.isEmpty()) {

        }

        JsonObject response = requests.remove(0);


        if(response.has("exception")){
            if(response.get("exception").getAsString().equals("timer"))
                throw new EndTimerException();
            else if (response.get("exception").getAsString().equals("move"))
                throw new MoveStoppedException();
        }
        return response.get("code").getAsBoolean();


    }

    public int askWhichPattern(Pattern[] patterns){

        JsonObject jason = new JsonObject();
        jason.addProperty("code","patterns");
        jason.addProperty("array",gason.toJson(patterns));

        writeOutput(jason);

        while (requests.isEmpty()) {

        }

        JsonObject response = requests.remove(0);
        return response.get("code").getAsInt();


    }

    public int askDraftPos() throws EndTimerException, MoveStoppedException {
        JsonObject request = new JsonObject();
        request.addProperty("code","draftpos");
        writeOutput(request);


        while (requests.isEmpty()) {

        }

        JsonObject response = requests.remove(0);


        if(response.has("exception")){
            if(response.get("exception").getAsString().equals("timer"))
                throw new EndTimerException();
            else if (response.get("exception").getAsString().equals("move"))
                throw new MoveStoppedException();
        }
        return response.get("code").getAsInt();

    }

    public void startTurn() {

        JsonObject request = new JsonObject();
        request.addProperty("code","startTurn");
        writeOutput(request);

    }

    public void updateCurrentPlayer(int currentPlayer) {

        JsonObject request = new JsonObject();
        request.addProperty("code","update");
        request.addProperty("what","currentPlayer");
        request.addProperty("current",currentPlayer);
        writeOutput(request);

    }

    public void updateTokens(ArrayList<Integer> tokens) {

        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","tokens");
        request.addProperty("tokens",gason.toJson(tokens));
        writeOutput(request);
    }

    public void updateGameTurn(Turns turn) {
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","gameTurn");
        request.addProperty("turn",turn.name());
        writeOutput(request);

    }


    public void updatePlayerState(Player p, int pos) {

        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code", "update");
        request.addProperty("what", "playerList");
        request.addProperty("pos", pos);
        request.addProperty("name", p.getUsername());
        request.addProperty("pass", p.getPassword());
        request.addProperty("numTok", p.getNumTokens());
        request.addProperty("score", p.getScore());
        request.addProperty("moves", gason.toJson(p.getMoves()));
        request.addProperty("currentT", gason.toJson(p.getCurrentTurn()));
        request.addProperty("privoc", gason.toJson(p.getPrivateObjectiveCard()));

        //request.addProperty("playerList", gason.toJson(players.get(i)));
        writeOutput(request);

    }

    public void updateWindowsFrame(WindowFrame wf) {
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","window");
        request.addProperty("window",gason.toJson(wf.getCells()));
        request.addProperty("row",wf.getRow());
        request.addProperty("col",wf.getCol());


        writeOutput(request);

    }

    public void updateDraftPool(ArrayList<Dice> draft) {
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","draft");
        request.addProperty("draft",gason.toJson(draft));
        writeOutput(request);

    }

    public void updateRoundTrack(HashMap<Integer,ArrayList<Dice>> track) {
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","roundtrack");
        request.addProperty("roundtrack",gason.toJson(track));
        writeOutput(request);

    }

    private void updateTools(ArrayList<Integer> tools){
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","tools");
        request.addProperty("tools",gason.toJson(tools));
        writeOutput(request);

    }

    private void updatePublicObj(ArrayList<Integer> poc){
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","poc");
        request.addProperty("poc",gason.toJson(poc));
        writeOutput(request);

    }

    private void updatePlayerList(ArrayList<Player> players){
        for(int i=0;i<players.size();i++) {
            JsonObject request = new JsonObject();
            Gson gason = new Gson();
            Player p = players.get(i);
            request.addProperty("code", "update");
            request.addProperty("what", "playerList");
            request.addProperty("pos", i);
            request.addProperty("name", p.getUsername());
            request.addProperty("pass", p.getPassword());
            request.addProperty("numTok", p.getNumTokens());
            request.addProperty("score", p.getScore());
            request.addProperty("moves", gason.toJson(p.getMoves()));
            request.addProperty("currentT", gason.toJson(p.getCurrentTurn()));
            request.addProperty("privoc", gason.toJson(p.getPrivateObjectiveCard()));
            //request.addProperty("playerList", gason.toJson(players.get(i)));
            writeOutput(request);
            updateWf(p.getWindowFrame(),i);

        }

    }

    private void updateCurrentRound(int round){
        JsonObject request = new JsonObject();
        request.addProperty("code","update");
        request.addProperty("what","round");
        request.addProperty("round",round);
        writeOutput(request);

    }
    public void updateGame(Game game) {
        updateTools(game.getToolCards());
        updateRoundTrack(game.getTrack().getTrack());
        updateCurrentRound(game.getCurrentRound());
        updateGameTurn(game.getCurrentTurn());
        updatePublicObj(game.getPublicObjectiveCards());
        updatePlayerList(game.getPlayers());
        updateTokens( game.getToolTokens());
        updateDraftPool(game.getDraft().getDraft());
        updateCurrentPlayer(game.getCurrentPlayer());


        for (int i = 0; i<game.getPlayers().size();i++) {
            updateWf(game.getPlayers().get(i).getWindowFrame(),i);
        }

    }

    private void updateWf(WindowFrame wf, int pos){
        JsonObject request = new JsonObject();
        Gson gason = new Gson();
        request.addProperty("code","update");
        request.addProperty("what","wf");
        request.addProperty("pos",pos);
        request.addProperty("wf",gason.toJson(wf.getCells()));
        request.addProperty("row",wf.getRow());
        request.addProperty("col",wf.getCol());
        writeOutput(request);

    }

    public void endTimer() {


        JsonObject jason = new JsonObject();
        jason.addProperty("code","timer");
        writeOutput(jason);
    }

    public void endGame(ArrayList<Integer> ranking) {


        JsonObject jason = new JsonObject();
        Gson gason = new Gson();
        jason.addProperty("code","endGame");
        jason.addProperty("rank",gason.toJson(ranking));
        writeOutput(jason);
    }

    public void printDice(Dice dice) {
        JsonObject jason = new JsonObject();
        Gson gason = new Gson();
        jason.addProperty("code","singleDice");
        jason.addProperty("dice",gason.toJson(dice));
        writeOutput(jason);
    }

    public void sendDisconnectionOf(String username) {
        JsonObject request = new JsonObject();
        request.addProperty("code","disconnection");
        request.addProperty("name",username);
        writeOutput(request);


    }

    public void sendConnectionOf(String username) {
        JsonObject request = new JsonObject();
        request.addProperty("code","connection");
        request.addProperty("name",username);
        writeOutput(request);
    }


    /**
     * metodo sincrozzato per scrivere sullo stream di output
     * @param output json object da scrivere sullo stream
     */
    public synchronized void writeOutput(Object output){
        outputStream.println(output);
        outputStream.flush();
    }
}

