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
        System.out.println(Thread.currentThread().getName());
        System.out.println("Conectado");

        try {
            System.out.println("Passou");
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            while(true){
                String nomeArquivo = entrada.readUTF();


                String pastaDoServidor = "C:/Users/Rodrigo/IdeaProjects/sercvico-tcp/";
                String caminhoDoArquivo = pastaDoServidor + nomeArquivo;

                File arquivo = new File(caminhoDoArquivo);;
                if (arquivo.exists()) {
                    System.out.println("Existe");
                    String nomeDoArquivo = arquivo.getName();
                    long tamanhoDoArquivo = arquivo.length();
                    String hashDoArquivo = calcularHash(arquivo);
                    String dadosDoArquivo = lerArquivo(arquivo);

                    System.out.println(nomeDoArquivo);
                    System.out.println(tamanhoDoArquivo);
                    System.out.println(hashDoArquivo);

                    DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                    saida.writeUTF("Nome do arquivo: " + nomeDoArquivo +
                            "\n" + "Tamanho: " + tamanhoDoArquivo + " bytes"
                            + "\n" + "Hash: " + hashDoArquivo + "\n" + "Status: ok" + "\n" + "Dados: " + dadosDoArquivo
                    );


                } else if (nomeArquivo == "-1") {

                    System.out.println("Saiu");
                    entrada.close();
                    socket.close();

                }
                else {
                    System.out.println("Nao Existe");

                    DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                    saida.writeUTF("Status: arquivo inexistente");

                }}

        } catch (Exception e) {
            e.printStackTrace();
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
