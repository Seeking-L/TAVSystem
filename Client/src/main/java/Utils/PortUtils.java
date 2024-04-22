package Utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortUtils {
    /*
     * LJQ copy from https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
     * */
    public static boolean isPortAvailable(int port) {//if a port is available.
        if (port < 1024 || port > 49151) {
            return false;
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    //to find an available server port
    public static int selectServerPort(){
        int i=8888;
        while(!isPortAvailable(i)){
            i++;
        }
        return i;
    }
}
