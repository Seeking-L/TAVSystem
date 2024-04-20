import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class UdpSendThread implements Runnable {

    private InetSocketAddress remoteReceiver;//对方的接收端口
    private DatagramSocket udpVideoSender;
    private DatagramPacket datagramPacket;

    public UdpSendThread(InetSocketAddress remoteReceiver) {
        this.remoteReceiver = remoteReceiver;
    }

    @Override
    public void run() {

        Thread audioSendThread=new Thread(new AudioSendThread(remoteReceiver));
        audioSendThread.start();

        try {
            udpVideoSender = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Camera camera=new Camera("我,发送给"+remoteReceiver);
        Webcam webcam = Webcam.getDefault();
//        webcam.open();
        //视频是由多照片组成，所以持续获得照片
        while (!Thread.interrupted()) {
            BufferedImage image = webcam.getImage();
            //将照通过网络发送
            try {
                trans(image);
            } catch (InterruptedException e) {
                System.out.println("udp sender 正在关闭camera 和 webcam");
                webcam.close();
                camera.close();
                System.out.println("udp sender 正在关闭audio");
                audioSendThread.interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println("udp sender 正在关闭camera 和 webcam");
            webcam.close();
            camera.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            System.out.println("udp sender 正在关闭audio");
            audioSendThread.interrupt();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void trans(BufferedImage image) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", byteArrayOutputStream);
        }catch (IllegalArgumentException e){
//            e.printStackTrace();
            System.out.println("no image data!");
            return;
        }

        byte[] data = byteArrayOutputStream.toByteArray();

        //将data的长度（int）转换为一个byte数组，插到data的最前端
        ByteBuffer byteBuffer = ByteBuffer.allocate(2*Integer.BYTES);
        byteBuffer.putInt(0,1);//数字1代表这是一个Video数据包
        byteBuffer.putInt(4,data.length);
        byte[] info = byteBuffer.array();
        byte[] newData = new byte[info.length + data.length];
        System.arraycopy(info, 0, newData, 0, info.length);
        System.arraycopy(data, 0, newData, info.length, data.length);
        //TODO--调试用
//        System.out.println("send:-----data.len: "+data.length+"-----len.len: "+info.length+"-----newData.len:"+newData.length);
        datagramPacket = new DatagramPacket(newData, 0, newData.length, remoteReceiver);
        udpVideoSender.send(datagramPacket);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw e;
        }
    }



}

