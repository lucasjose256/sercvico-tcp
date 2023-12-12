import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

class Janela extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField nomeArquivoField;
    private JTextField IPField;
    private JTextField PortaField;
    private JTextArea infoArquivoArea;
    private boolean conexao;
    private boolean arquivoRecebido;
    /*private DataOutputStream saida;

    private DataInputStream entrada;
    private Socket socket;*/

    public Janela() {
        /*this.socket = socket;
        this.saida = saida;
        this.entrada = entrada;*/

        conexao = true;
        arquivoRecebido = false;
        Container janela = getContentPane();
        setLayout(null);

        JLabel labelNomeArquivo = new JLabel("Nome do Arquivo: ");
        labelNomeArquivo.setBounds(50, 5, 120, 20);

        JLabel labelIP = new JLabel("IP: ");
        labelIP.setBounds(50, 30, 120, 20);
        nomeArquivoField = new JTextField();
        nomeArquivoField.setBounds(180, 5, 150, 20);

        JLabel labelPorta = new JLabel("Porta: ");
        labelPorta.setBounds(50, 55, 150, 20);
        IPField = new JTextField();
        IPField.setBounds(180, 30, 150, 20);
        PortaField = new JTextField();
        PortaField.setBounds(180, 55, 150, 20);

        JButton buttonSolicitar = new JButton("Solicitar Arquivo");
        buttonSolicitar.setBounds(50, 80, 150, 30);

        infoArquivoArea = new JTextArea();
        infoArquivoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArquivoArea);
        scrollPane.setBounds(50, 120, 300, 100);

        JButton buttonSair = new JButton("Sair");
        buttonSair.setBounds(50, 240, 100, 30);

        janela.add(labelNomeArquivo);
        janela.add(nomeArquivoField);
        janela.add(IPField);
        janela.add(PortaField);
        janela.add(buttonSolicitar);
        janela.add(scrollPane);
        janela.add(buttonSair);
        janela.add(labelIP);
        janela.add(labelPorta);

        buttonSolicitar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nomeArquivo = nomeArquivoField.getText();
                String IP = IPField.getText();
                String Porta = PortaField.getText();
                System.out.println("solicitado nome " + nomeArquivoField.getText());
                try {
                    solicitarArquivo(nomeArquivo, IP, Porta);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonSair.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conexao = false;
                dispose();
            }
        });

        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }




    public void displayFileInformation(String info) {
        infoArquivoArea.setText(info);
    }

    public void solicitarArquivo(String nomeArquivo, String IP, String Porta) throws IOException {
        System.out.println("Arquivo: " + nomeArquivo + " Solicitado em: " + IP + ":" + Porta);
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(IP);
        int serverPort = Integer.parseInt(Porta);
        String fileName = nomeArquivo;
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
        /*try (FileOutputStream fos = new FileOutputStream("arquivoSalvo.txt")) {
            fos.write(receiveBuffer);
        }*/

        /*socket.receive(receivePacket);
        String r = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(r);

        // Close the socket*/
        FileOutputStream fos = new FileOutputStream("arquivoSalvo.txt");
        int i = 0;
        socket.setSoTimeout(2000);
        while (true) {
            try {socket.receive(receivePacket);
                //String r = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(i);
                i++;
                String r = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(r.length());
                fos.write(receivePacket.getData(), 0, receivePacket.getLength());
                if (receivePacket.getLength() == 0 || receivePacket.getData().length == 0) {
                    break;
                }
                System.out.println("Loop");
            }
            catch (Exception e)
            {break;}
        }
        System.out.println("Saiu");
        fos.close();
        byte[] checkFile = readFileToByteArray(new File("arquivoSalvo.txt"));
        byte check = calculateChecksum(checkFile);
        System.out.println(((int) check & 0xFF));
        socket.close();
    }
    private static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {

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
    public void saveDataToFile(String input, String filename) throws IOException {
        int lastIndex = input.lastIndexOf("Dados: ");

        if (lastIndex != -1) {
            String dados = input.substring(lastIndex + "Dados: ".length());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(dados);
            }

            System.out.println("Dados gravados com sucesso em " + filename);
        } else {
            System.out.println("String não contém a marca 'Dados: '.");
        }
    }
}
