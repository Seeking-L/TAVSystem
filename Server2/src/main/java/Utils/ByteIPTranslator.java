package Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * reference : https://blog.csdn.net/a417930422/article/details/51372813
 */
public class ByteIPTranslator {
    /**
     * 将byte数组转为ip字符串
     * @param src
     * @return xxx.xxx.xxx.xxx
     */
    public static String bytesToIp(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }

    /**
     * 将ip字符串转为byte数组,注意:ip不可以是域名,否则会进行域名解析
     * @param ip
     * @return byte[]
     * @throws UnknownHostException
     */
    public static byte[] ipToBytes(String ip) throws UnknownHostException {
        return InetAddress.getByName(ip).getAddress();
    }
}
