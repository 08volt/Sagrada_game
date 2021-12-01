package progettoIngSW.Network.Client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import progettoIngSW.Exceptions.EndTimerException;
import progettoIngSW.Exceptions.MoveStoppedException;
import progettoIngSW.Model.Colors;
import progettoIngSW.Model.Pattern;
import progettoIngSW.ViewInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.*;

/**
 * Questa classe che estende thread è responsabile di rispondere alle varie richieste del player controller
 * riguardo ai vari parametri necessari per l'uso delle tool card invocando direttamente i metodi sulla view.
 */

public class ClientSocketHelperThread extends Thread{

    private static ViewInterface view;
    private static PrintWriter outputStream;

    public static List<JsonObject> commands = Collections.synchronizedList(new ArrayList<>());


    /**
     * costruttore della classe
     * @param viewInterface l'oggetto view dove chiedere i parametri all'utente
     * @param printWriter dove scrivere la risposta al server
     */

    public ClientSocketHelperThread(ViewInterface viewInterface, PrintWriter printWriter){
        view = viewInterface;
        outputStream = printWriter;
    }

    /**
     * legge le richieste da server, chiede all'utente, e risponde al server.
     * gestione della comunicazione con il server tramite l'uso di JsonObject.
     * @see ClientSocket run
     */

    @Override
    public void run() {
        while(true){
            Gson gason = new Gson();

            while(commands.isEmpty()){

            }
            if(!commands.isEmpty()) {
                JsonObject request = commands.remove(0);

                JsonObject response = new JsonObject();

                if (request.get("code").getAsString().equals("increase")) {
                    boolean risp = false;
                    try {
                        risp = view.askIncrease();
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");
                    } catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }

                    response.addProperty("code", risp);
                    outputStream.println(response); //BOOL
                    outputStream.flush();


                } else if (request.get("code").getAsString().equals("windowpos")) {
                    int i = 0;
                    boolean placement = request.get("placement").getAsBoolean();
                    try {
                        i = view.askWindowPos(placement);
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");
                    }catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }
                    response.addProperty("code", i);
                    outputStream.println(response); //BOOL
                    outputStream.flush();

                } else if (request.get("code").getAsString().equals("trackpos")) {
                    int[] t = new int[0];
                    try {
                        t = view.askRoundTrackPos();
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");

                    }catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }
                    response.addProperty("code", gason.toJson(t));
                    outputStream.println(response); //BOOL
                    outputStream.flush();

                } else if (request.get("code").getAsString().equals("number")) {
                    Colors col = gason.fromJson(request.get("color").getAsString(), Colors.class);

                    int n = 0;
                    try {
                        n = view.askNumber(col);
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");
                    }catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }
                    response.addProperty("code", n);
                    outputStream.println(response);
                    outputStream.flush();
                } else if (request.get("code").getAsString().equals("howmany")) {
                    boolean risp = false;
                    try {
                        risp = view.askHowMany();
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");

                    }catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }
                    response.addProperty("code", risp);
                    outputStream.println(response); //BOOL
                    outputStream.flush();
                } else if (request.get("code").getAsString().equals("patterns")) {
                    Pattern[] patterns = gason.fromJson(request.get("array").getAsString(), Pattern[].class);

                    int p = view.askWhichPattern(patterns);

                    response.addProperty("code", p);
                    outputStream.println(response); //BOOL
                    outputStream.flush();
                } else if (request.get("code").getAsString().equals("draftpos")) {
                    int d = 0;
                    try {
                        d = view.askDraftPos();
                    } catch (EndTimerException e) {
                        response.addProperty("exception", "timer");

                    }catch (MoveStoppedException e) {
                        response.addProperty("exception", "move");
                    }
                    response.addProperty("code", d);
                    outputStream.println(response);
                    outputStream.flush();

                }
            }


        }
    }
}
