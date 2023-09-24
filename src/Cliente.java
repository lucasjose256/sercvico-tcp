import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        // Abre uma conexão com o servidor
        Socket socket = new Socket("192.168.18.6", 54321);
        // Define a stream de saída de dados do cliente
        DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
        DataInputStream entrada = new DataInputStream(socket.getInputStream());

        Janela janela = new Janela(socket, saida); // Passa o socket e a stream de saída para a janela
        janela.setVisible(true);

        // Não fecha o socket aqui; ele será fechado quando o botão "Sair" for pressionado
    }
}