### 计网大作业：IP电话  ———— 带服务端版本

### java socket 编程实现的IP电话：支持text、audio、video
### ~~开发环境：win10+IDEA+jdk17~~
### 开发环境：win10+IDEA+**jdk1.8**
> 注意：当前版本将jdk从17改为了1.8。原因是在打包exe文件时，开发者发现开发者的jdk17中无自带jre，而命令生成的jre会使得本项目中使用到的webcam包报错。于是改为使用jdk1.8。
### ~~exe文件运行所需环境：安装jdk，版本17以上~~
<br>

> ~~由于目前在将Server部署到云服务器时，仍会有udp无法传给client的bug，所以此项目目前只支持局域网下的Server。~~
<br>
> 经过菜鸟开发者重新复习nat的运行原理，终于解决了bug。本项目可以在云服务器上部署Server。

#### 不带服务器版本见：
> https://github.com/Seeking-L/TAVNode


#### 运行前修改：
- Server中：由于private key的保密需要，未将Keys类上传。运行前需要添加Keys类，并填写数据库的用户名以及密码。 Keys类如下：<br>
- ![image](READMEImgs/1.png)<br><br>
- ![image](READMEImgs/2.png)

- Client中：<br>
    > ~~由于目前云端服务器尚有bug未修改完成,只能在局域网下本机运行服务器，Server的IP和port无法确定。故：Client在运行前，需要修改Client中保存的Server的IP及port。~~
    <br>
    > 请根据自己的服务器地址进行修改
    
    在运行Client前：<br>
  -  首先运行Server，检查Server的打印输出，确定Server是port号。(一般是8888)<br>
  - 确定Server所在的IP。<br>
  - 修改Client中的ServerInfo类，修正Server的IP与port。<br>
   ![image](READMEImgs/3.png)



### 关于打包成exe(Client)：
#### 参考博客
> https://blog.csdn.net/zmq836010/article/details/124257340
> https://cloud.tencent.com/developer/article/2248453
#### exe4j配置文件(Client)(请检查文件路径)及文件图标: 
> /exe4jconfig/Client.exe4j