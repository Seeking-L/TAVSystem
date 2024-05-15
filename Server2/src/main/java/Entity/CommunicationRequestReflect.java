package Entity;

/**
 * flag=4：server→client，反馈选择好友的结果。1个int：1代表好友在线，0代表不在线。
 */
public class CommunicationRequestReflect extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=4;
    private boolean isFriendAvailable;
    private int friendId;

    public CommunicationRequestReflect(boolean isFriendAvailable, int friendId) {
        this.isFriendAvailable = isFriendAvailable;
        this.friendId = friendId;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        CommunicationRequestReflect.flag = flag;
    }

    public boolean isFriendAvailable() {
        return isFriendAvailable;
    }

    public void setFriendAvailable(boolean friendAvailable) {
        isFriendAvailable = friendAvailable;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "CommunicationRequestReflect{" +
                "isFriendAvailable=" + isFriendAvailable +
                ", friendId=" + friendId +
                '}';
    }
}
