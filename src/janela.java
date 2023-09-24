import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Janela extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField nomeArquivoField;
    private JTextArea infoArquivoArea;
    private boolean conexao;
    private boolean arquivoRecebido;
    private DataOutputStream saida;
    private Socket socket;

    public Janela(Socket socket, DataOutputStream saida) {
        this.socket = socket;
        this.saida = saida;

        conexao = true;
        arquivoRecebido = false;
        Container janela = getContentPane();
        setLayout(null);

        JLabel labelNomeArquivo = new JLabel("Nome do Arquivo: ");
        labelNomeArquivo.setBounds(50, 40, 120, 20);

        nomeArquivoField = new JTextField();
        nomeArquivoField.setBounds(180, 40, 150, 20);

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
        janela.add(buttonSolicitar);
        janela.add(scrollPane);
        janela.add(buttonSair);

        buttonSolicitar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nomeArquivo = nomeArquivoField.getText();
                System.out.println("solicitado nome " + nomeArquivoField.getText());
                solicitarArquivo(nomeArquivo);
            }
        });

        buttonSair.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conexao = false;
                try {
                    saida.writeUTF("Sair"); // Informa ao servidor que o cliente está saindo
                    saida.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dispose();
            }
        });

        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public boolean isConnected() {
        return conexao;
    }

    public boolean hasReceivedFile() {
        return arquivoRecebido;
    }

    public void setFileReceived(boolean received) {
        arquivoRecebido = received;
    }

    public void displayFileInformation(String info) {
        infoArquivoArea.setText(info);
    }

    public void solicitarArquivo(String nomeArquivo) {
        try {
            saida.writeUTF(nomeArquivo);
            saida.flush();
            DataInputStream entrada= new DataInputStream(socket.getInputStream());
            String info = entrada.readUTF();
            displayFileInformation(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}