import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

class Janela extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField nomeArquivoField;
    private JTextArea infoArquivoArea;
    private boolean conexao;
    private boolean arquivoRecebido;
    private DataOutputStream saida;

    private DataInputStream entrada;
    private Socket socket;

    public Janela(Socket socket, DataOutputStream saida, DataInputStream entrada) {
        this.socket = socket;
        this.saida = saida;
        this.entrada = entrada;

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
                    saida.writeUTF("-1"); // Informa ao servidor que o cliente está saindo
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
            String info = entrada.readUTF();
            displayFileInformation(info);
            saveDataToFile(info, ("Gravacao" + nomeArquivo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDataToFile(String input, String filename) throws IOException {
        // Encontre a última aparição de "Dados: " na string
        int lastIndex = input.lastIndexOf("Dados: ");

        if (lastIndex != -1) {
            // Pegue os dados após a última aparição de "Dados: "
            String dados = input.substring(lastIndex + "Dados: ".length());

            // Grave os dados em um arquivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(dados);
            }

            System.out.println("Dados gravados com sucesso em " + filename);
        } else {
            System.out.println("String não contém a marca 'Dados: '.");
        }
    }
}
