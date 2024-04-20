import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

//refer to https://blog.csdn.net/weixin_42168430/article/details/109257345    ---LJQ
public class AudioSendThread implements Runnable{

    private InetSocketAddress remoteReceiver;//对方的接收端口
    private DatagramSocket udpAudioSender;
    private DatagramPacket datagramPacket;
    private AudioFormat format;
    private TargetDataLine targetDataLine;

    public AudioSendThread(InetSocketAddress remoteReceiver) {
        this.remoteReceiver = remoteReceiver;
        try {
            this.udpAudioSender = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.format = new AudioFormat(22050, 16, 1, true, false);
    }

    @Override
    public void run() {//提供wave格式信息
        try {
            targetDataLine = AudioSystem.getTargetDataLine(format);
            targetDataLine.open(format);
            targetDataLine.start();
            System.out.println("----收音中----");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[1024];//缓存音频数据
        System.out.println("开始发送语音");
        while (!Thread.interrupted()){
            int len = 0;
            while(len!=-1) {
                len = targetDataLine.read(data, 0, data.length);//捕获录音数据

                if(len!=-1) {
                    //将data的长度（int）转换为一个byte数组，插到data的最前端
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2*Integer.BYTES);
                    byteBuffer.putInt(0,2);//数字2代表这是一个audio数据包
                    byteBuffer.putInt(4,len);
                    byte[] info = byteBuffer.array();
                    byte[] newData = new byte[info.length + data.length];
                    System.arraycopy(info, 0, newData, 0, info.length);
                    System.arraycopy(data, 0, newData, info.length, data.length);
                    //TODO--调试用
//        System.out.println("send:-----data.len: "+data.length+"-----len.len: "+len.length+"-----newData.len:"+newData.length);
                    datagramPacket = new DatagramPacket(newData, 0, newData.length, remoteReceiver);
                    try {
                        udpAudioSender.send(datagramPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        targetDataLine.close();
    }

    //录制音频
    public void getAudio() {//提供wave格式信息
        try {
            AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format);
            targetDataLine.open(format);
            targetDataLine.start();
            System.out.println("----收音中----");
            //直接播放出来
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
            sourceDataLine.open(format);
            sourceDataLine.start();
            System.out.println("播放中");
            //开子线程进行播放
            byte[] b = new byte[128];//缓存音频数据
            new Thread(new Runnable() {
                public void run() {
                    int a = 0;
                    while(a!=-1) {
                        System.out.println("录制中");
                        a = targetDataLine.read(b, 0, b.length);//捕获录音数据
                        if(a!=-1) {
                            sourceDataLine.write(b, 0, a);//播放录制的声音
                        }
                    }
                }
            }).start();
        }catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
