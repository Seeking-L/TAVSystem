package Threads;

import Entity.TextMessageFromClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class TcpInputScan implements Runnable{
    private Scanner scanner;
    private ObjectOutputStream tcpOut;
    private int communicationDesId;//联络对象的ID
    private String communicationDesName;//联络对象Name

    public TcpInputScan(ObjectOutputStream tcpOut, int communicationDesId, String communicationDesName) {
        this.scanner = new Scanner(System.in);
        this.tcpOut = tcpOut;
        this.communicationDesId = communicationDesId;
        this.communicationDesName = communicationDesName;
    }

    @Override
    public void run() {
        while(true){
            if(scanner.hasNext()){
                System.out.println();
                String message=scanner.nextLine();
                if(message.equals("/bye")){//结束程序
                    System.exit(0);
                }
                TextMessageFromClient textMessageFromClient=new TextMessageFromClient(communicationDesId,message);
                try {
                    tcpOut.writeObject(textMessageFromClient);
                    tcpOut.flush();
                }catch (IOException e){
                    System.out.println("--------------------信息发送失败！--------------------");
                    e.printStackTrace();
                }
            }
        }
    }
}
