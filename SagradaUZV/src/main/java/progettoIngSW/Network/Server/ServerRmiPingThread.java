package progettoIngSW.Network.Server;

import progettoIngSW.Network.Client.ClientInterface;

import java.rmi.RemoteException;


/**
 * Thread che si occupa do fare un "ping" ai client che usano RMI per capire subito quando uno di questi si è sconnesso
 */
public class ServerRmiPingThread extends Thread {


    ServerRMI serverRMI;
    Server server;

    public ServerRmiPingThread(ServerRMI rmi, Server s){
        serverRMI = rmi;
        server = s;


    }

    /**
     * prova a eseguire un motodo vuoto in tutte le client interface registrate nel server rmi
     */

    @Override
    public void run() {
        while(true){
            try {
                synchronized (this) {
                    wait(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ClientInterface[] c = new ClientInterface[0];
            try {
                c = serverRMI.getClientsMap().values().toArray(c);
            }catch(NullPointerException ignored){
            }
            for(int i = 0; i< c.length; i++){
                try {
                    c[i].ping();
                } catch (RemoteException e2) {
                    if(e2.getClass() == java.rmi.ConnectException.class){

                        try{
                            server.clientDisconnection(serverRMI.usernameFromHashmap(c[i]));
                        }catch (Exception ignored){

                        }
                    }
                }
            }

        }
    }


}
