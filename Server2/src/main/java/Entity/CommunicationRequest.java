package Entity;

/**
 * flag=3：client→server，选择好友进行联系。int 好友ID
 */
public class CommunicationRequest extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=3;
    private int friendId;

    @Override
    public int getFlag() {
        return flag;
    }


    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public CommunicationRequest() {
    }

    public CommunicationRequest(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "CommunicationRequest{" +
                "friendId=" + friendId +
                '}';
    }
}
