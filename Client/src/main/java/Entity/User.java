package Entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID=1L;
    private String userName;
    private String IP;
    private int port;//recieving port for both TCP and UDP
    private int userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User() {
    }

    public User(String userName, String IP, int port, int userId) {
        this.userName = userName;
        this.IP = IP;
        this.port = port;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", IP='" + IP + '\'' +
                ", port=" + port +
                ", userId=" + userId +
                '}';
    }
}
