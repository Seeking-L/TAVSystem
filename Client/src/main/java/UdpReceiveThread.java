import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UdpReceiveThread implements Runnable {
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */

    private AudioFormat format;
    private SourceDataLine sourceDataLine;
    private DatagramSocket udpReceiver;
    private DatagramPacket datagramPacket;
    private int localReceiverPort;//local udp receiver's port
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private JFrame window;
    private Graphics g;

    public UdpReceiveThread(int localReceiverPort) throws SocketException {
        this.format = new AudioFormat(22050, 16, 1, true, false);
        System.out.println("----receiving video:  " + localReceiverPort + "----");
        this.localReceiverPort = localReceiverPort;
        this.udpReceiver = new DatagramSocket(localReceiverPort);
        window = new JFrame();
    }

    @Override
    public void run() {
        //audio准备
        try {
            sourceDataLine = AudioSystem.getSourceDataLine(format);
            sourceDataLine.open(format);
            sourceDataLine.start();
            System.out.println("audio播放开始");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


        //video准备
        this.window.setTitle("Receiver: " + localReceiverPort);
        this.window.setSize(new Dimension(640, 480));
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.g = this.window.getGraphics();


        while (!Thread.interrupted()) {
            byte[] bytes = new byte[1024 * 60];
            datagramPacket = new DatagramPacket(bytes, 0, bytes.length);
            try {
                udpReceiver.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = datagramPacket.getData();

            //从data中先读出realdata的长度，再独户realdata
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            //读出第一个int，分辨这是一个video数据包还是audio数据包。1代表video，2代表audio
            int VAFlag = byteBuffer.getInt();

            if (VAFlag == 1) {//Video数据包
                int realDataLength = byteBuffer.getInt();

//                System.out.println("receive: realDataLength: " + realDataLength);//TODO---调试用
                try {
                    trans(Arrays.copyOfRange(data, 8, realDataLength + 8));
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (VAFlag == 2) {//Audio数据包
                int len = byteBuffer.getInt();
                sourceDataLine.write(data, 8, len+8);//播放声音
            }
        }

        System.out.println("udp receiver 正在关闭camera 和 webcam");
        sourceDataLine.close();
        window.dispose();
    }


    public void trans(byte[] data) throws IOException, InterruptedException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(stream);
        g.drawImage(image, 0, 0, null);//将读到的图片进行绘制
//            System.out.println(read == bytes.length);
//        System.out.println("data.length: " + data.length);//TODO----调试用
    }

}
