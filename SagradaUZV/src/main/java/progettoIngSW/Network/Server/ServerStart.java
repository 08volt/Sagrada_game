package progettoIngSW.Network.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerStart {


    public static void main(String[] args) throws RemoteException {

        int playerTimer = 30;
        int turnTimer = 90;
        int patternTimer = 30;


        try {
            if(args.length == 3){
                playerTimer = Integer.parseInt(args[0]);
                turnTimer = Integer.parseInt(args[1]);
                patternTimer = Integer.parseInt(args[2]);

            }
            if(args.length == 2){
                playerTimer = Integer.parseInt(args[0]);
                turnTimer = Integer.parseInt(args[1]);
            }else if(args.length == 1){
                playerTimer = Integer.parseInt(args[0]);
            }

        }catch(Exception ignored){

        }
        Server s = new Server(playerTimer,turnTimer,patternTimer);

        ServerRMI serverRMI = null;
        ServerSocketImpl serverSocket;

        try {
            serverRMI = new ServerRMI(s);
            s.setServerRMI(serverRMI);
        } catch (RemoteException e) {
            System.out.println("server RMI non attivo");
        }
        serverSocket = new ServerSocketImpl(s);
        s.setServerSocket(serverSocket);
        serverSocket.start();


        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(1099);
            if(serverRMI != null)
                System.out.println(serverRMI.toString());
        } catch (RemoteException e) {
            System.out.println("server RMI non attivo");
        }

        try {
            if(registry != null)
                registry.rebind("server", serverRMI);
        } catch (RemoteException | NullPointerException e) {
            System.out.println("server RMI non attivo");
        }

        s.start();

    }
}
