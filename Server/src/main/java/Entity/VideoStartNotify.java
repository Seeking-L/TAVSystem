package Entity;

/**
 * flag=9：server→client，通知即将开启UDP通信。int: server's Udp port
 */
public class VideoStartNotify extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=9;
    private int serverUdpPort;

    public VideoStartNotify(int serverUdpPort) {
        this.serverUdpPort = serverUdpPort;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        VideoStartNotify.flag = flag;
    }

    public int getServerUdpPort() {
        return serverUdpPort;
    }

    public void setServerUdpPort(int serverUdpPort) {
        this.serverUdpPort = serverUdpPort;
    }

    @Override
    public String toString() {
        return "VideoStartNotify{" +
                "serverUdpPort=" + serverUdpPort +
                '}';
    }
}
