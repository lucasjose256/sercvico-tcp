import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;

public class ThreadSockets extends Thread {
    private Socket socket;

    public ThreadSockets(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        super.run();
        System.out.println(Thread.currentThread().getName()); // Imprime o nome da thread atual.

        try {
            OutputStream out = socket.getOutputStream();

            // Caminho para o arquivo que você deseja enviar
            String filePath = "C:\\Users\\lucas\\Downloads\\linux.txt";

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Obtém o hash como um array de bytes


            File fileToSend = new File(filePath);

            // Lê o arquivo e o envia para o cliente
            FileInputStream fileInputStream = new FileInputStream(fileToSend);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                md.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = md.digest();

            // Converte o hash em uma representação hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte hashByte : hashBytes) {
                sb.append(String.format("%02x", hashByte));
            }

            // Exibe o hash
            String hashValue = sb.toString();
            // Crie um objeto PrintWriter para enviar a string
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream()); // Cria um fluxo de saída de dados para o socket.
            saida.writeUTF(hashValue);
            System.out.println("Hash SHA-256 do arquivo: " + hashValue);
            saida.close();
            fileInputStream.close();
            out.close();
            socket.close();

            System.out.println("Arquivo enviado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace(); // Trata qualquer exceção lançada durante o processo e imprime o rastreamento da pilha.
        }
    }
}
