package progettoIngSW.View.Cli;

import java.util.Scanner;

public class ThreadConsoleReader extends Thread {

    Scanner in;

    public  ThreadConsoleReader(){
        in = new Scanner(System.in);
    }


    @Override
    public void run() {
        while(true){

            String line = in.nextLine();
            CLI.commands.add(line);

        }
    }
}
