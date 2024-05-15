### 计网大作业：IP电话  ———— 带服务器版本
<br>

#### 原始版本见：
> https://github.com/Seeking-L/TAVNode

#### 关于Server与Server2的说明：
> 本系统原始设想的支持text、video、audio通信，其中text通信由于一些技术问题一直卡住没有实现（主要是多线程和scanner阻塞问题，暂时没有解决）。所以决定为了先把作业交上，剔除text通信功能。Server2中除去了text功能，保留了video、audio通信。
>> Server2中，用户连接上好友后，双方直接开启音视频通信，关闭了用户的text输入。<br>
（以后如果有时间）一定补上

#### 运行前修改：
Server中：由于private key的保密需要，未将Keys类上传。运行前需要添加Keys类，并填写数据库的用户名以及密码。 Keys类如下：

![image](READMEImgs/1.png)

![image](READMEImgs/2.png)
