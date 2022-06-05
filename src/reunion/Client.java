package reunion;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    /*public static void main(String[] args) {
        Client client = new Client();
        client.sendMessage("dfghujik");
    }

     */

    void sendMessage(String message) {
        PrintStream fluxSortie = null;
        try{
            InetAddress address = InetAddress.getByName("127.0.0.5");
            Socket socket = new Socket(address, 8888);
            fluxSortie = new PrintStream(socket.getOutputStream());
            fluxSortie.println(message);

            //Scanner fluxEntre = new Scanner(socket.getInputStream());
            //String rep = fluxEntre.nextLine();
            //System.out.println(rep);
            //socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
