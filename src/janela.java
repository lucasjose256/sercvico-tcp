import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

class Janela extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField nomeArquivoField;
    private JTextField IPField;
    private JTextField PortaField;
    private JTextArea infoArquivoArea;
    private boolean conexao;
    private boolean arquivoRecebido;

    private boolean breakConnection;
    /*private DataOutputStream saida;

    private DataInputStream entrada;
    private Socket socket;*/

    public Janela() {
        /*this.socket = socket;
        this.saida = saida;
        this.entrada = entrada;*/

        conexao = true;
        arquivoRecebido = false;
        breakConnection = false;
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
        buttonSair.setBounds(250, 240, 100, 30);
        JButton breakCon = new JButton("Encerrar Conexao");
        breakCon.setBounds(50, 240, 150, 30);

        janela.add(labelNomeArquivo);
        janela.add(nomeArquivoField);
        janela.add(IPField);
        janela.add(PortaField);
        janela.add(buttonSolicitar);
        janela.add(scrollPane);
        janela.add(buttonSair);
        janela.add(breakCon);
        janela.add(labelIP);
        janela.add(labelPorta);
        breakCon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                breakConnection = true;
            }
        });
        buttonSolicitar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nomeArquivo = nomeArquivoField.getText();
                String IP = IPField.getText();
                String Porta = PortaField.getText();
                System.out.println("solicitado nome " + nomeArquivoField.getText());
                try {
                    solicitarArquivo(nomeArquivo, IP, Porta);
                } catch (IOException | InterruptedException ex) {
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

    public void solicitarArquivo(String nomeArquivo, String IP, String Porta) throws IOException, InterruptedException {
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
        if(response.contains("arquivo inexistente")) {
            socket.close();
            return;
        }
        int size = Integer.parseInt((response.replaceAll("(?s).*Tamanho: ", "")).replaceAll(" bytes(?s).*", ""));
        socket.receive(receivePacket);
        String checkSumserver = new String(receivePacket.getData(), 0, receivePacket.getLength());
        socket.receive(receivePacket);
        String tamanho = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Teste: " + tamanho);
        int siz = Integer.parseInt(tamanho);
        System.out.println("Numeros de buffers: " + siz);
        int buffercheck[] = new int[siz];
        byte[] file = new byte[size];
        String save = "image.png";
        FileOutputStream fos = new FileOutputStream(save);
        socket.setSoTimeout(2000);
        int z = 0;
        while (!breakConnection) {
            Random random = new Random();
            Thread.sleep(random.nextInt(150));
            try {
                socket.receive(receivePacket);
                int d = Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));
                System.out.println("Recebi: " + d);
                System.out.println(d);
                buffercheck[d] = 1;
                socket.receive(receivePacket);
                String r = new String(receivePacket.getData(), 0, receivePacket.getLength());
                //fos.write(receivePacket.getData(), 0, receivePacket.getLength());

                System.arraycopy(receivePacket.getData(), 0, file, d*1024, receivePacket.getLength());
                //if(z > 15)
                //    break;
                if (receivePacket.getLength() == 0 || receivePacket.getData().length == 0) {
                    break;
                }
            }
            catch (Exception e)
            {break;}
        }
        System.out.println("Saiu");
        fos.write(file);
        fos.close();
        byte[] checkFile = readFileToByteArray(new File(save));
        byte check = calculateChecksum(checkFile);
        String checksumClient = "" + ((int) check & 0xFF);
        System.out.println(((int) check & 0xFF));
        breakConnection = false;
        socket.close();
        String sequence = "";
        while(!checkSumserver.equals(checksumClient)) {
            for (int j = 0; j < siz; j++) {
                if (buffercheck[j] == 0)
                    sequence += j + ",";
            }
            sequence = sequence.substring(0, sequence.length() - 1);
            String sinal = "-1";
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            byte[] byt = sinal.getBytes();
            sendPacket = new DatagramPacket(byt, byt.length, serverAddress, serverPort);
            socket.send(sendPacket);

            byt = nomeArquivo.getBytes();
            sendPacket = new DatagramPacket(byt, byt.length, serverAddress, serverPort);
            socket.send(sendPacket);


            byte[] data = sequence.getBytes();

            int chunkSize = 1024;
            int totalChunks = (data.length + chunkSize - 1) / chunkSize;

            for (int k = 0; k < totalChunks; k++) {
                int start = k * chunkSize;
                int end = Math.min((k + 1) * chunkSize, data.length);

                byte[] chunk = new byte[end - start];
                System.arraycopy(data, start, chunk, 0, chunk.length);

                sendPacket = new DatagramPacket(chunk, chunk.length, serverAddress, serverPort);
                socket.send(sendPacket);
            }
            data = sinal.getBytes();
            sendPacket = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(sendPacket);
            while(true) {
                try {
                    socket.receive(receivePacket);

                    String a = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Pos: " + a);
                    if(a.length() > 30)
                        socket.receive(receivePacket);
                        a = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    //System.out.println(a);
                    int pos = Integer.parseInt(a);
                    if(buffercheck[pos] != 1) {
                        socket.receive(receivePacket);
                        int length = receivePacket.getLength();
                        System.arraycopy(receivePacket.getData(), 0, file, pos * 1024, length);
                        System.out.println("Escreveu tamanho:" + length);
                        buffercheck[pos] = 1;
                    }
                } catch (Exception e) {
                    break;
                }

            }
            System.out.println("Fim");
            Path path = FileSystems.getDefault().getPath(save);
            Files.delete(path);
            fos = new FileOutputStream(save);
            fos.write(file);
            fos.close();
            checkFile = readFileToByteArray(new File(save));
            check = calculateChecksum(checkFile);
            checksumClient = "" + ((int) check & 0xFF);
            System.out.println("checksumClient: " + checksumClient + " checksumServer: " + checkSumserver);
            socket.close();
        }
        //fos.write(file);
        //fos.close();
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
