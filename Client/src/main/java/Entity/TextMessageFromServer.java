package Entity;

/**
 * flag=7：server→client，文本通信。int 源用户的ID,String message
 */
public class TextMessageFromServer extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=7;

    private int srcId;//源用户的ID

    private String message;

    public TextMessageFromServer(int srcId, String message) {
        this.srcId = srcId;
        this.message = message;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        TextMessageFromServer.flag = flag;
    }

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TextMessageFromServer{" +
                "srcId=" + srcId +
                ", message='" + message + '\'' +
                '}';
    }
}
