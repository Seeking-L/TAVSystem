package Entity;

/**
 * flag=6：client→server，文本通信。int 目的用户的ID,String message
 */
public class TextMessageFromClient extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=6;
    private int desId;//目的用户的ID
    private String message;

    public TextMessageFromClient(int desId, String message) {
        this.desId = desId;
        this.message = message;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        TextMessageFromClient.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDesId() {
        return desId;
    }

    public void setDesId(int desId) {
        this.desId = desId;
    }

    @Override
    public String toString() {
        return "TextMessageFromClient{" +
                "desId=" + desId +
                ", message='" + message + '\'' +
                '}';
    }
}
