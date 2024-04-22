package Entity;

import java.io.Serializable;

public class TcpMessage implements Serializable {
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
