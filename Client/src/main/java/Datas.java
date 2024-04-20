import java.net.ServerSocket;

public class Datas {
    private int localReceiverPort;//this node's port while it work as a receiver.(for both TCP and UDP)
    private int remoteReceiverPort;//对方的receiver socket的端口
    private String remoteIP;//对方的IP地址

    public int getLocalReceiverPort() {
        return localReceiverPort;
    }

    public void setLocalReceiverPort(int localReceiverPort) {
        this.localReceiverPort = localReceiverPort;
    }

    public int getRemoteReceiverPort() {
        return remoteReceiverPort;
    }

    public void setRemoteReceiverPort(int remoteReceiverPort) {
        this.remoteReceiverPort = remoteReceiverPort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }
}
