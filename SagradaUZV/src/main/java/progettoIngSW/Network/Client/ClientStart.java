package progettoIngSW.Network.Client;

import progettoIngSW.View.Cli.CLI;
import progettoIngSW.View.Gui.GUI;

import java.util.Scanner;

/**
 * classe di avvio dell'applicazione lato client.
 * chiede all'utente il tipo di view che desidera usare.
 */

public class ClientStart {



    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Client c = null;
        System.out.println("Welcome to Sagrada!");

        String message = "What view interface do you prefer to use?\n1: CLI\n2: GUI";
        System.out.println(message);
        String choice = in.nextLine();

        while(!isInteger(choice) || (Integer.parseInt(choice) != 1 && Integer.parseInt(choice) != 2)) {

            System.out.println("Wrong input, please insert a correct value: ");
            choice = in.nextLine();


        }

        switch (Integer.parseInt(choice)){
            case 1:
                CLI cli = new CLI();
                c = new Client(cli);
                break;
            case 2:
                GUI gui = new GUI();
                c = new Client(gui);
                Thread t = new Thread(gui);
                t.start();
                break;
        }

        Thread th = new Thread(c);
        th.start();

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
