import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        //Abre um conexão com o servidor
        Socket socket= new Socket("192.168.3.3",54321);
        //define a stream de saida de dados do cliente
        // Fluxo de entrada para receber o arquivo
        InputStream in = socket.getInputStream();

        // Caminho onde você deseja salvar o arquivo recebido
        String filePath = "C:\\Users\\lucas\\IdeaProjects\\servico_tcp\\out/arquivo.txt";

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        // Crie um objeto BufferedReader para ler a string do servidor
        BufferedReader text = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Leia a string do servidor
        String receivedMessage = text.readLine();
        DataInputStream entrada= new DataInputStream(socket.getInputStream());
        String mensagem= entrada.readUTF();

        System.out.println("Mensagem recebida do servidor: " + mensagem);

        // Feche os recursos
        in.close();
        fileOutputStream.close();
        socket.close();

        System.out.println("Arquivo recebido com sucesso!");

        //fechar o socket
        socket.close();


    }
}
