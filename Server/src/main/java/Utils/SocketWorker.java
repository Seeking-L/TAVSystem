package Utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 用于表示与用户之间的socket联系。
 * 在socket上绑定一个isCommunicating，用以表示该用户是否已经在和另一个用户进行联络
 */
public class SocketWorker {
    private Socket socket;
    private ObjectOutputStream tcpOut;
    private ObjectInputStream tcpIn;

    public SocketWorker(Socket socket, ObjectOutputStream tcpOut, ObjectInputStream tcpIn) {
        this.socket = socket;
        this.tcpOut = tcpOut;
        this.tcpIn = tcpIn;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getTcpOut() {
        return tcpOut;
    }

    public void setTcpOut(ObjectOutputStream tcpOut) {
        this.tcpOut = tcpOut;
    }

    public ObjectInputStream getTcpIn() {
        return tcpIn;
    }

    public void setTcpIn(ObjectInputStream tcpIn) {
        this.tcpIn = tcpIn;
    }
}
