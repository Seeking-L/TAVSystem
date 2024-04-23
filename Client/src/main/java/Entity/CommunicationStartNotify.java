package Entity;

/**
 * flag=5：server→client，告知被发起者开启通话。int 发起者ID
 */
public class CommunicationStartNotify extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=5;
    private int friendId;//发起者的id

    public CommunicationStartNotify(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        CommunicationStartNotify.flag = flag;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "CommunicationStartNotify{" +
                "friendId=" + friendId +
                '}';
    }
}
