import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Description:
 * @Author: xuyp
 * @Date: 2019/7/19 16:06
 * @Modified By:
 */
public class Camera {

    private static JFrame window;
    private Webcam webcam;

    public Camera(String jFrameName)  {

//        final Webcam webcam = Webcam.getDefault();
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        window = new JFrame(jFrameName);
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.pack();
        window.setVisible(true);


        final JButton button = new JButton("拍照");
        window.add(panel, BorderLayout.CENTER);
        window.add(button, BorderLayout.SOUTH);
        window.setResizable(true);
        window.pack();
        window.setVisible(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {

                button.setEnabled(true);  //设置按钮不可点击


                //实现拍照保存-------start
                String fileName = "D://" + System.currentTimeMillis();       //保存路径即图片名称（不用加后缀）
                WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null, "拍照成功");
                        button.setEnabled(true);    //设置按钮可点击

                        return;
                    }
                });
                //实现拍照保存-------end

            }
        });
    }

    public void close(){
        webcam.close();
        window.dispose();
    }



}