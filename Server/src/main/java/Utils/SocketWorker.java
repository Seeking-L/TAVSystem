package Utils;

import java.net.Socket;

/**
 * 用于表示与用户之间的socket联系。
 * 在socket上绑定一个isCommunicating，用以表示该用户是否已经在和另一个用户进行联络
 */
public class SocketWorker {
    private Socket socket;
    private boolean isCommunicating;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isCommunicating() {
        return isCommunicating;
    }

    public void setCommunicating(boolean communicating) {
        isCommunicating = communicating;
    }

    public SocketWorker(Socket socket, boolean isCommunicating) {
        this.socket = socket;
        this.isCommunicating = isCommunicating;
    }
}
