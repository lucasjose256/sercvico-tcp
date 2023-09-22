import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

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
            DataInputStream entrada = new DataInputStream(socket.getInputStream()); // Cria um fluxo de entrada de dados a partir do socket.
            String mensagem = entrada.readUTF(); // Lê uma mensagem do fluxo de entrada.

            String novaMensagem = mensagem.toUpperCase(); // Converte a mensagem para maiúsculas.

            DataOutputStream saida = new DataOutputStream(socket.getOutputStream()); // Cria um fluxo de saída de dados para o socket.
            saida.writeUTF(novaMensagem); // Escreve a mensagem em maiúsculas no fluxo de saída.

            entrada.close(); // Fecha o fluxo de entrada de dados.
            saida.close(); // Fecha o fluxo de saída de dados.
            socket.close(); // Fecha o socket, encerrando a conexão.

        } catch (Exception e) {
            e.printStackTrace(); // Trata qualquer exceção lançada durante o processo e imprime o rastreamento da pilha.
        }
    }
}
