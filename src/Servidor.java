import javax.sound.midi.Sequence;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Servidor {
    public static void main(String[] args) throws InterruptedException {
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

                if(nomeArquivo.equals("-1"))
                    RepairFile(datagramSocket, receivePacket.getAddress(), receivePacket.getPort(), nomeArquivo);
                else
                sendFileToClient(datagramSocket, receivePacket.getAddress(), receivePacket.getPort(), nomeArquivo);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    private static void RepairFile(DatagramSocket socket, InetAddress clientAddress, int clientPort, String fileName)
            throws IOException, NoSuchAlgorithmException, InterruptedException {

        byte[] receiveBuffer = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        String nomeArquivo = new String(receivePacket.getData(), 0, receivePacket.getLength());
        File arquivo = new File(nomeArquivo);
        byte[] fileData = readFileToByteArray(arquivo);
        String sequence = "";
        while(true) {
            socket.receive(receivePacket);
            String t = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (t.equals("-1"))
                break;
            sequence += t;
        }
        System.out.println(sequence);
        String[] numbersArray = sequence.split(",");

        int[] intArray = new int[numbersArray.length];
        for (int i = 0; i < numbersArray.length; i++) {
            intArray[i] = Integer.parseInt(numbersArray[i]);
        }
        try {
            for (int i = 0; i < intArray.length; i++) {
                int aux = 0;
                aux = intArray[i];
                String a = "" + aux;
                System.out.println("A: " + a);
                byte[] byt = a.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(byt, byt.length, clientAddress, clientPort);
                socket.send(sendPacket);
                int size = 0;
                if (fileData.length < (aux * 1024 + 1024) && fileData.length % 1024 != 0 && fileData.length - (aux * 1024) > 0) {
                    size = fileData.length - (aux * 1024);
                    System.out.println("Size A: " + size);
                } else {
                    size = 1024;
                    System.out.println("Size B: " + size);
                }
                byte[] send = new byte[size];
                System.arraycopy(fileData, aux * 1024, send, 0, size);
                sendPacket = new DatagramPacket(send, send.length, clientAddress, clientPort);
                socket.send(sendPacket);
                Thread.sleep(10);
            }
            Path path = Path.of("teste.png");
            //Files.write(path, fileData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (Exception e){return;}
    }


    private static void sendFileToClient(DatagramSocket socket, InetAddress clientAddress, int clientPort, String fileName)
            throws IOException, NoSuchAlgorithmException {
        String pastaDoServidor = "C:/Users/Rodrigo/IdeaProjects/sercvico-udp/";
        String caminhoDoArquivo = pastaDoServidor + fileName;

        File arquivo = new File(caminhoDoArquivo);

        if (arquivo.exists()) {
            System.out.println("File exists");
            String nomeDoArquivo = arquivo.getName();
            long tamanhoDoArquivo = arquivo.length();
            String hashDoArquivo = calcularHash(arquivo);

            byte[] fileData = readFileToByteArray(arquivo);
            byte checksum = calculateChecksum(fileData);

            String response = "Nome do arquivo: " + nomeDoArquivo +
                    "\n" + "Tamanho: " + tamanhoDoArquivo + " bytes" +
                    "\n" + "Hash: " + hashDoArquivo + "\n" + "Checksum: " + ((int) checksum & 0xFF) + "\n" + "Status: ok" + "\n";

            byte[] responseData = response.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            socket.send(sendPacket);
            response = "" + ((int) checksum & 0xFF);
            responseData = response.getBytes();
            sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            socket.send(sendPacket);

            // Create a DatagramPacket to send the file data to the client
            String aux = "" + ((int)Math.ceil(fileData.length / 1024) + 1);
            DatagramPacket indexT = new DatagramPacket(aux.getBytes(), aux.getBytes().length, clientAddress, clientPort);
            socket.send(indexT);
            try (FileInputStream fis = new FileInputStream(arquivo);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer;
                int bytesRead = 0;
                int i = 0;
                while (i < Math.floor(fileData.length/1024)) {
                    buffer = new byte[1024];
                    String ind = "" + i;
                    DatagramPacket index = new DatagramPacket(ind.getBytes(), ind.getBytes().length, clientAddress, clientPort);
                    socket.send(index);
                    System.arraycopy(fileData, i*1024, buffer, 0, buffer.length);
                    DatagramPacket filePacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    socket.send(filePacket);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    i++;
               }
                if(fileData.length % 1024 != 0)
                {
                    String ind = "" + i;
                    DatagramPacket index = new DatagramPacket(ind.getBytes(), ind.getBytes().length, clientAddress, clientPort);
                    socket.send(index);
                    buffer = new byte[fileData.length - (int)Math.floor(fileData.length/1024)*1024];
                    System.arraycopy(fileData, i*1024, buffer, 0, buffer.length);
                    DatagramPacket filePacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    socket.send(filePacket);
                }
            }
            System.out.println("File sent to client");
        } else {
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