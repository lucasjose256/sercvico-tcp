import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        //Abre um conexão com o servidor
        Socket socket= new Socket("192.168.3.3",54321);
        //define a stream de saida de dados do cliente
        DataOutputStream saida= new DataOutputStream(socket.getOutputStream());

        saida.writeUTF("lucas");
        //define a stream de entrada de dados do cliente
        DataInputStream entrada= new DataInputStream(socket.getInputStream());
        String mensagem= entrada.readUTF();
        System.out.println(mensagem);

        //fecha streams de entrada e saídad de dados
        entrada.close();
        saida.close();

        //fechar o socket
        socket.close();


    }
}
