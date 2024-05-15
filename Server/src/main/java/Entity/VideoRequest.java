package Entity;

/**
 * flag=8：client->server，用户要求发起video通信。
 */
public class VideoRequest extends TcpMessage{
    private static final long serialVersionUID=1L;
    private static int flag=8;

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        VideoRequest.flag = flag;
    }
}
