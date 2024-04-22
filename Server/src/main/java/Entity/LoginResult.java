package Entity;

import java.util.LinkedList;


/**
 * flag=2：server→client，回复登录。boolean isSuccess，（若登录成功）int userId，LinkedList<User> friends
 */
public class LoginResult extends TcpMessage{
    private int flag;
    private boolean isSuccess;
    private int userId;
    private LinkedList<User> friends;

    public LoginResult() {
        this.flag=2;
    }

    public LoginResult(boolean isSuccess) {
        this.flag=2;
        this.isSuccess = isSuccess;
    }

    public LoginResult(boolean isSuccess, int userId, LinkedList<User> friends) {
        this.flag=2;
        this.isSuccess = isSuccess;
        this.userId = userId;
        this.friends = friends;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LinkedList<User> getFriends() {
        return friends;
    }

    public void setFriends(LinkedList<User> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "flag=" + flag +
                ", isSuccess=" + isSuccess +
                ", userId=" + userId +
                ", friends=" + friends +
                '}';
    }
}
