package progettoIngSW.Network.Server;

import com.google.gson.JsonObject;
import progettoIngSW.Exceptions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * thread che si occupa di eseguire l'uso della tool card e inviare una risposta al client (connessione socket)
 */

public class ServerSocketHelperThread extends Thread {


    public final List<JsonObject> toolCommands = Collections.synchronizedList(new ArrayList<>());

    private Server server;
    private String username;
    private ServerSocketHandler sh;

    public ServerSocketHelperThread(Server s, ServerSocketHandler sh){
        server = s;
        this.username = sh.getUsername();
        this.sh = sh;
    }

    @Override
    public void run() {
        while(true){

            while(toolCommands.isEmpty()){}

            JsonObject request = toolCommands.remove(0);
            JsonObject response = new JsonObject();
            int posTool = request.get("pos").getAsInt();
            response.addProperty("what","tool");
            try {

                server.useToolCard(posTool,username);
                response.addProperty("code","ok");
                sh.writeOutput(response);

            } catch (DiceNotFoundException e) {
                response.addProperty("code","dicenotfound");
                sh.writeOutput(response);


            } catch (RulesBreakException e) {
                response.addProperty("code","rules");
                response.addProperty("type",e.getType().name());
                sh.writeOutput(response);

            } catch (CellNotEmptyException e) {
                response.addProperty("code","cellnotempty");
                sh.writeOutput(response);


            } catch (NotPlayingException e) {
                response.addProperty("code","notplaying");
                sh.writeOutput(response);

            } catch (InvalidParamsException e) {
                response.addProperty("code","invalidparams");
                sh.writeOutput(response);

            } catch (NotValidCellException e) {
                response.addProperty("code","notvalidcell");
                sh.writeOutput(response);

            } catch (DraftFullException e) {
                response.addProperty("code","draftfull");
                sh.writeOutput(response);

            } catch (EndTimerException e) {
                response.addProperty("code","endTimerExc");
                sh.writeOutput(response);

            }
        }
    }
}
