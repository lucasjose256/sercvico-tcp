import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Servidor {
    public static void main(String[] args) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(54321, InetAddress.getByName("192.168.18.6"));
            System.out.println("A porta 54321 foi aberta");

            while (true) {
                byte[] receiveBuffer = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(receivePacket);
                System.out.println("Client connected: " + receivePacket.getAddress() + ":" + receivePacket.getPort());
                String nomeArquivo = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received request for file: " + nomeArquivo);

                sendFileToClient(datagramSocket, receivePacket.getAddress(), receivePacket.getPort(), nomeArquivo);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileToClient(DatagramSocket socket, InetAddress clientAddress, int clientPort, String fileName)
            throws IOException, NoSuchAlgorithmException {
        String pastaDoServidor = "C:/Users/Rodrigo/IdeaProjects/sercvico-tcp/";
        String caminhoDoArquivo = pastaDoServidor + fileName;

        File arquivo = new File(caminhoDoArquivo);

        if (arquivo.exists()) {
            System.out.println("File exists");
            String nomeDoArquivo = arquivo.getName();
            long tamanhoDoArquivo = arquivo.length();
            String hashDoArquivo = calcularHash(arquivo);

            // Read the file content into a byte array

            // Calculate checksum
            byte[] fileData = readFileToByteArray(arquivo);
            byte checksum = calculateChecksum(fileData);

            // Prepare the response message
            String response = "Nome do arquivo: " + nomeDoArquivo +
                    "\n" + "Tamanho: " + tamanhoDoArquivo + " bytes" +
                    "\n" + "Hash: " + hashDoArquivo + "\n" + "Checksum: " + ((int) checksum & 0xFF) + "\n" + "Status: ok" + "\n";

            // Convert the response message to a byte array
            byte[] responseData = response.getBytes();

            // Create a DatagramPacket to send the response to the client
            DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            socket.send(sendPacket);

            // Create a DatagramPacket to send the file data to the client


            try (FileInputStream fis = new FileInputStream(arquivo);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    DatagramPacket filePacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    System.out.println("Bytes: " + bytesRead);
                    socket.send(filePacket);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    buffer = new byte[1024];
                }
            }







            /*DatagramPacket filePacket = new DatagramPacket(fileData, fileData.length, clientAddress, clientPort);
            socket.send(filePacket);*/

            System.out.println("File sent to client");
        } else {
            // If the file does not exist, send an error response to the client
            String response = "Status: arquivo inexistente";
            byte[] responseData = response.getBytes();
            DatagramPacket errorPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            socket.send(errorPacket);

            System.out.println("File does not exist. Error sent to client");
        }
    }

    private static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                //System.out.println("Buffer1 length: " + buffer.length);
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private static byte calculateChecksum(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += b;
        }
        // Truncate to 8 bits (byte)
        byte checksum = (byte) sum;

        return checksum;
    }

    private static String calcularHash(File arquivo) throws IOException, NoSuchAlgorithmException {
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
}