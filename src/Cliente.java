import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Janela janela = new Janela();
        janela.setVisible(true);

    }

    /*public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("192.168.18.6");
        int serverPort = 54321;

        // Replace "example.txt" with the actual file name you want to send
        String fileName = "example.txt";

        // Convert the file name to bytes
        byte[] fileNameBytes = fileName.getBytes();

        // Create a DatagramPacket to send the file name to the server
        DatagramPacket sendPacket = new DatagramPacket(fileNameBytes, fileNameBytes.length, serverAddress, serverPort);
        socket.send(sendPacket);

        // Receive the response from the server
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);

        // Convert the received data to a string and print it
        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Response from server: " + response);

        // Close the socket
        socket.close();
    }*/
}