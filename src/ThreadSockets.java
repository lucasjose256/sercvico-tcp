import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ThreadSockets extends Thread{
private Socket socket;
public ThreadSockets(Socket s){
    this.socket=s;
}

    @Override
    public void run() {
        super.run();
        System.out.println(Thread.currentThread().getName());

        try{
            DataInputStream entrada= new DataInputStream(socket.getInputStream());
            String mensagem=entrada.readUTF();
            String novaMensagem=mensagem.toUpperCase();

            DataOutputStream saida= new DataOutputStream(socket.getOutputStream());
            saida.writeUTF(novaMensagem);
            entrada.close();
            saida.close();

            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
