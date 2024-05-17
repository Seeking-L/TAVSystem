package Threads;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Flag:
 * 1: user1 发给 user2的video数据
 * 2: user1 发给 user2的audio数据
 * 3: user2 发给 user1的video数据
 * 4: user2 发给 user1的audio数据
 */
public class UdpThread implements Runnable {

    int Id1;//user1's id
    int Id2;//user2's id
    //send
    private InetSocketAddress remoteReceiver1;//user1的接收端口
    private InetSocketAddress remoteReceiver2;//user2的接收端口
    private DatagramSocket udpSender;
    private DatagramPacket sendDataPacket;

    //receive
    private DatagramSocket udpReceiver;
    private DatagramPacket receivedDatagramPacket;
    private int localReceiverPort;//local udp receiver's port


    public UdpThread(int Id1,int Id2,int localReceiverPort, InetSocketAddress remoteReceiver1
            , InetSocketAddress remoteReceiver2
    ) throws SocketException {
        System.out.println("----receiving video:  " + localReceiverPort + "----");
        this.Id1=Id1;
        this.Id2=Id2;
        this.remoteReceiver1 = remoteReceiver1;
        this.remoteReceiver2 = remoteReceiver2;
        this.localReceiverPort = localReceiverPort;
        this.udpReceiver = new DatagramSocket(localReceiverPort);
        this.udpSender =new DatagramSocket();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            byte[] bytes = new byte[1024 * 60];
            receivedDatagramPacket = new DatagramPacket(bytes, 0, bytes.length);
            try {
                udpReceiver.receive(receivedDatagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = receivedDatagramPacket.getData();

            //从data中先读出realdata的长度，再读出realdata
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            //读出第一个int，分辨这是哪个用户发送的
            int Flag = byteBuffer.getInt();

            if (Flag == Id1) {// 1->2 的数据包
                int realDataLength = byteBuffer.getInt();
                System.out.println("1->2: realDataLength: " + realDataLength);//TODO---调试用
                try {
                    transmit(remoteReceiver2,Arrays.copyOfRange(data, 4, realDataLength + 12));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Flag == Id2) {// 2->1 数据包
                int realDataLength = byteBuffer.getInt();
                System.out.println("2->1: realDataLength: " + realDataLength);//TODO---调试用
                try {
                    transmit(remoteReceiver1,Arrays.copyOfRange(data, 4, realDataLength + 12));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        udpReceiver.close();
        udpSender.close();
        System.out.println("udp receiver 正在关闭");
    }


    public void transmit(InetSocketAddress remoteReceiver,byte[] data) throws IOException {
        System.out.println("send-to: "+remoteReceiver);
        sendDataPacket = new DatagramPacket(data, 0, data.length, remoteReceiver);
        udpReceiver.send(sendDataPacket);
//        udpSender.send(sendDataPacket);
    }

}
