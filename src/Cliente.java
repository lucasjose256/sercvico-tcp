import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.18.10", 54321);
        DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
        DataInputStream entrada = new DataInputStream(socket.getInputStream());
        Janela janela = new Janela(socket, saida, entrada);
        janela.setVisible(true);

    }
}