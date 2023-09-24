import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class ThreadSockets extends Thread {
    private Socket socket;

    public ThreadSockets(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        super.run();
        System.out.println(Thread.currentThread().getName()); // Imprime o nome da thread atual.
        System.out.println("Conectado");

        try {
            System.out.println("Passou");
            DataInputStream entrada = new DataInputStream(socket.getInputStream()); // Cria um fluxo de entrada de dados a partir do socket.
            while(true){
            String nomeArquivo = entrada.readUTF(); // Lê o nome do arquivo do fluxo de entrada.

            // Verifica se o arquivo existe na pasta do servidor
            String pastaDoServidor = "C:/Users/Rodrigo/IdeaProjects/sercvico-tcp/";
            String caminhoDoArquivo = pastaDoServidor + nomeArquivo;


            File arquivo = new File(caminhoDoArquivo);
            if (arquivo.exists() && arquivo.isFile()) {
                System.out.println("Existe");
                // Arquivo encontrado, fornecer informações sobre o arquivo
                String nomeDoArquivo = arquivo.getName();
                long tamanhoDoArquivo = arquivo.length();
                String hashDoArquivo = calcularHash(arquivo);
                String dadosDoArquivo = lerArquivo(arquivo);

                System.out.println(nomeDoArquivo);
                System.out.println(tamanhoDoArquivo);
                System.out.println(hashDoArquivo);
                System.out.println(dadosDoArquivo);

                // Enviar informações do arquivo para o cliente
                DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                saida.writeUTF("Nome do arquivo: " + nomeDoArquivo +
                        "\n" + "Tamanho: " + tamanhoDoArquivo + " bytes"
                        + "\n" + "Hash: " + hashDoArquivo + "\n" + "Status: ok" + "\n" + "Dados: " + dadosDoArquivo
                        );


            } else if (entrada.readUTF() == "-1") {
                entrada.close();
                socket.close();

            }
            else {
                System.out.println("Nao Existe");
                // Arquivo não encontrado
                DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                saida.writeUTF("Status: arquivo inexistente");
                //saida.close();
            }}

        } catch (Exception e) {
            e.printStackTrace(); // Trata qualquer exceção lançada durante o processo e imprime o rastreamento da pilha.
        }
    }

    private String calcularHash(File arquivo) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(arquivo)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String lerArquivo(File arquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
    }
}
