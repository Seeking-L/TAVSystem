package Threads;

import Utils.ByteIPTranslator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class UdpRecieveThread implements Runnable{
    private HashMap<InetSocketAddress, LinkedList<byte[]>> packetQueue;
    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket;

    public UdpRecieveThread(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            byte[] bytes = new byte[1024 * 60];
            datagramPacket = new DatagramPacket(bytes, 0, bytes.length);
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = datagramPacket.getData();

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            String IP = ByteIPTranslator.bytesToIp(Arrays.copyOfRange(data, 0, 4));
            System.out.println("IP :" + IP);
            int port = byteBuffer.getInt(4);
            System.out.println("port: " + port);
            InetSocketAddress inetSocketAddress=new InetSocketAddress(IP,port);

            if(packetQueue.containsKey(inetSocketAddress)){
                packetQueue.get(inetSocketAddress).add(data);
            }
        }
    }
}
