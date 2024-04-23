package Entity;

/**
 * flag=1：client→server，进行登录。String username，String password
 */
public class TcpLogin extends TcpMessage {
    private static final long serialVersionUID=1L;
    private int flag;
    private String userName;
    private String password;

    public TcpLogin() {
        this.flag=1;
    }

    public TcpLogin(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.flag=1;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "TcpLogin{" +
                "flag=" + flag +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
