import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
//1 define o serverSocket(abrir porta de conexão)
        ServerSocket serverSocket= new ServerSocket(54321);
        System.out.println("a porta 54321 foi aberta");
      //2 aguarda solitação do cliente
        while (true) {
            Socket socket = serverSocket.accept();
    System.out.println("Cliente "+socket.getInetAddress().getHostAddress()+" conectado");
      ThreadSockets thread=new ThreadSockets(socket);
      thread.start();
        }
    }


}
