package reunion;

import javafx.scene.control.Label;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Serveur {

    String  reception(){
        try {
            ServerSocket server = new ServerSocket(8888);
            System.out.println("Reception en cours....!");
            Socket socket = server.accept();
            Scanner sc = new Scanner(socket.getInputStream());
            String message = sc.nextLine();
            //System.out.println("Le message reçu est : "+message);

            //PrintStream fluxSortie = new PrintStream(socket.getOutputStream());
            //fluxSortie.println("message reçu !");
            //socket.close();
            //server.close();
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    void reponse() {
        PrintStream fluxSortie = null;
        try{
            System.out.println("Entrez votre message svp !");
            String message = "Message reçu";
            InetAddress address = InetAddress.getByName("127.0.0.5");
            Socket socket = new Socket(address, 8888);
            fluxSortie = new PrintStream(socket.getOutputStream());
            fluxSortie.println(message);
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

