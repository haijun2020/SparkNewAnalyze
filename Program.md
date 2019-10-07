# 项目简介：
1.完成大数据项目的架构，安装部署，架构继承与开发，用户可视化交互设计  
2.完成实时在线数据分析

具体功能：
1.实时分析前20名流量最高的新闻话题  
2.实时同价当前线上已曝光的新闻话题  
项目采用的技术点：  
（无论用哪种版本的框架一定要了解各个版本是否兼容，建议去官网了解适配情况，或者采用CDH版本。以前年幼无知疯狂踩坑）  
Hadoop2.7、Zookeeper3.4.5、Flume1.7、HBase1.3.1、Kafka_2.10-0.9.0.0 Spark MySQL  
jdk1.8  
开发工具  
虚拟机：VMware、Centos6.7  
虚拟机ssh：SecureCRT  
IDEA  
# 项目架构：  

环境简介：  
利用VMware虚拟机+Centos6.7  
机器1：内存5G+20G  
机器2：内存3G+20G  
机器3：内存3G+20G  
![](https://github.com/Keysluomo/SparkNewAnalyze/blob/master/image/clipboard5.png)
Linux配置要点  

1）修改为静态IP  
在终端命令窗口中输入  
- [root@hadoop101 /]#vim /etc/udev/rules.d/70-persistent-net.rules  

进入如下页面，删除 eth0 该行；将 eth1 修改为 eth0，同时复制物理 ip 地址  
![](https://github.com/Keysluomo/SparkNewAnalyze/blob/master/image/clipboard6.png)

修改IP地址  
[root@hadoop101 /]# vim /etc/sysconfig/network-scripts/ifcfg-eth0  

需要修改的内容有6项： 
```
HWADDR=  
IPADDR=  
GATEWAY=  
ONBOOT=yes  
BOOTPROTO=static  
DNS1=8.8.8.8  
```

执行：  
[root@hadoop101 /]# service network restart  

如果报错，reboot，重启虚拟机。  
修改 linux 的 hosts 文件  

（1）进入Linux系统查看本机的主机名。通过 hostname 命令查看。  
12 [root@hadoop100 /]# hostnamehadoop100  

（2）如果感觉此主机名不合适，我们可以进行修改。通过编辑/etc/sysconfig/network文件。  
123456 [root@hadoop100~]# vi /etc/sysconfig/network修改文件中主机名称NETWORKING=yesNETWORKING_IPV6=noHOSTNAME= hadoop101注意：主机名称不要有“_”下划线

（3）打开此文件后，可以看到主机名。修改此主机名为我们想要修改的主机名  
hadoop101  

（4）保存退出。  

（5）打开/etc/hosts  
[root@hadoop100 ~]# vim /etc/hosts  
添加如下内容  
```
192.168.1.100 hadoop100  
192.168.1.101 hadoop101  
192.168.1.102 hadoop102  
192.168.1.103 hadoop103  
```

（6）并重启设备，重启后，查看主机名，已经修改成功  
2）修改 window10 的 hosts 文件  
（1）进入 C:\Windows\System32\drivers\etc 路径  
（2）打开 hosts 文件并添加如下内容
```
192.168.1.100 hadoop100  
192.168.1.101 hadoop101  
192.168.1.102 hadoop102  
192.168.1.103 hadoop103 
```
 
关闭防火墙  
1）查看防火墙开机启动状态  
[root@hadoop101 ~]# chkconfig iptables --list  
2）关闭防火墙  
[root@hadoop101 ~]# chkconfig iptables off  
在 opt 目录下创建文件  
2）设置 atguigu 用户具有 root 权限  
修改 /etc/sudoers 文件，找到下面一行，在 root 下面添加一行，如下所示：  
[root@hadoop101 atguigu]# vi /etc/sudoers## Allow root to run any commands anywhereroot    ALL=(ALL)     ALLatguigu   ALL=(ALL)       ALL
修改完毕，现在可以用 atguigu 帐号登录，然后用命令 su - ，即可获得 root 权限进行操作。  
3）在/opt 目录下创建文件夹  
（1）在 root 用户下创建 module、software 文件夹  
[root@hadoop101 opt]# mkdir module[root@hadoop101 opt]# mkdir software  
（2）修改 module、software 文件夹的所有者 
```
[root@hadoop101 opt]# chown atguigu:atguigu module
[root@hadoop101 opt]# chown atguigu:atguigu sofrware
[root@hadoop101 opt]# ls -al
总用量 16
drwxr-xr-x.  6 root    root 4096 4 月  24 09:07 .
dr-xr-xr-x. 23 root    root 4096 4 月  24 08:52 ..
drwxr-xr-x.  4 root    root 4096 4 月  23 16:26 module
drwxr-xr-x.  2 root    root 4096 4 月  23 16:25 software
```

### 安装 jdk  
1）卸载现有 jdk  
（1）查询是否安装 java 软件：  
 [root@hadoop101 opt]# rpm -qa|grep java  
（2）如果安装的版本低于 1.7，卸载该 jdk  
 [root@hadoop101 opt]# rpm -e 软件包  
2）用 SecureCRT 工具将 jdk、Hadoop-2.7.2.tar.gz 导入到 opt 目录下面的 software 文件夹下面  
3）在 linux 系统下的 opt 目录中查看软件包是否导入成功  
 [root@hadoop101opt]# cd software/[root@hadoop101software]# lshadoop-2.7.2.tar.gz  jdk-8u144-linux-x64.tar.gz  
4）解压 jdk 到/opt/module 目录下  
 [root@hadoop101software]# tar -zxvf jdk-8u144-linux-x64.tar.gz -C /opt/module/  
5）配置 jdk 环境变量  
（1）先获取 jdk 路径：  
 [root@hadoop101 jdk1.8.0_144]# pwd/opt/module/jdk1.8.0_144  
（2）打开/etc/profile 文件：  
 [root@hadoop101 jdk1.8.0_144]# vi /etc/profile  
在 profie 文件末尾添加 jdk 路径：   
 ##JAVA_HOMEexport JAVA_HOME=/opt/module/jdk1.8.0_144export PATH=$PATH:$JAVA_HOME/bin  
（3）保存后退出：  
:wq  
（4）让修改后的文件生效：  
 [root@hadoop101 jdk1.8.0_144]# source /etc/profile  
（5）重启（如果 java -version 可以用就不用重启）：  
 
6）测试 jdk 安装成功  
 [root@hadoop101 jdk1.8.0_144]# java -versionjava version "1.8.0_144"  
### 安装 Hadoop  
1）进入到 Hadoop 安装包路径下：  
[root@hadoop101 ~]# cd /opt/software/  
2）解压安装文件到/opt/module 下面  
 [root@hadoop101 software]# tar -zxf hadoop-2.7.2.tar.gz -C /opt/module/  
3）查看是否解压成功  
 [root@hadoop101 software]# ls /opt/module/hadoop-2.7.2  
4）在/opt/module/hadoop-2.7.2/etc/hadoop 路径下配置 hadoop-env.sh  
（1）Linux 系统中获取 jdk 的安装路径：  
 [root@hadoop101 jdk1.8.0_144]# echo $JAVA_HOME/opt/module/jdk1.8.0_144  
（2）修改 hadoop-env.sh 文件中 JAVA_HOME 路径：  
 [root@hadoop101 hadoop]# vi hadoop-env.sh  
修改 JAVA_HOME 如下 :  
 export JAVA_HOME=/opt/module/jdk1.8.0_144  
 5）将 hadoop 添加到环境变量  
（1）获取 hadoop 安装路径：  
[root@ hadoop101 hadoop-2.7.2]# pwd  
/opt/module/hadoop-2.7.2  
（2）打开/etc/profile 文件：  
```
[root@ hadoop101 hadoop-2.7.2]# vi /etc/profile  
在 profie 文件末尾添加 jdk 路径：（shitf+g）  
##HADOOP_HOME  
export HADOOP_HOME=/opt/module/hadoop-2.7.2  
export PATH=PATH:HADOOP_HOME/bin  
export PATH=PATH:HADOOP_HOME/sbin  
```

（3）保存后退出：  
:wq  
（4）让修改后的文件生效：  
 [root@ hadoop101 hadoop-2.7.2]# source /etc/profile  
（5）重启(如果 hadoop 命令不能用再重启)：  
 [root@ hadoop101 hadoop-2.7.2]# sync[root@ hadoop101 hadoop-2.7.2]# reboot  


8）配置 ssh   
ssh 连接时出现 Host key verification failed 的解决方法  
```
[root@hadoop101 opt]# ssh 192.168.1.103   
The authenticity of host '192.168.1.103 (192.168.1.103)' can't be established.   
RSA key fingerprint is cf:1e:de:d7:d0:4c:2d:98:60:b4:fd:ae:b1:2d:ad:06.   
Are you sure you want to continue connecting (yes/no)?    
Host key verification failed. 
```
  
 
解决方案如下：直接输入 yes   
### 无密钥配置   
（1）进入到我的 home 目录   
  [root@hadoop101 opt]$ cd ~/.ssh    

（2）生成公钥和私钥：   
[root@hadoop101 .ssh]$ ssh-keygen -t rsa    

然后敲（三个回车），就会生成两个文件 id_rsa（私钥）、id_rsa.pub（公钥）   
（3）将公钥拷贝到要免密登录的目标机器上 
```
[root@hadoop101 .ssh]$ ssh-copy-id hadoop102   
[root@hadoop101 .ssh]$ ssh-copy-id hadoop103   
[root@hadoop101 .ssh]$ ssh-copy-id hadoop101  
```


### 配置集群 
```
（1）core-site.xml   

[atguigu@hadoop101 hadoop]$ vi core-site.xml   
<!-- 指定 HDFS 中 NameNode 的地址 -->    
<property>     
<name>fs.defaultFS</name>        
 <value>hdfs://hadoop101:9000</value>  </property>   
 
 <!-- 指定 hadoop 运行时产生文件的存储目录 -->    
 <property>     
 <name>hadoop.tmp.dir</name>     
 <value>/opt/module/hadoop-2.7.2/data/tmp</value>    
 </property>   
 
（2）Hdfs   
  hadoop-env.sh   
[atguigu@hadoop102 hadoop]$ vi hadoop-env.sh   
export JAVA_HOME=/opt/module/jdk1.8.0_144   
  hdfs-site.xml   
<configuration>     
<property>     
<name>dfs.replication</name>     
<value>3</value>    
</property>   
 
 <property>         
 <name>dfs.namenode.secondary.http-address</name>           
 <value>hadoop103:50090</value>       
 </property>   
</configuration>   

slaves   
[atguigu@hadoop102 hadoop]$ vi slaves   
hadoop101   
hadoop102   
hadoop103   

 （3）yarn  
yarn-env.sh    
```
[atguigu@hadoop102 hadoop]$ vi yarn-env.sh   
export JAVA_HOME=/opt/module/jdk1.8.0_144   
  yarn-site.xml   
  [atguigu@hadoop101 hadoop]$ vi yarn-site.xml   
<configuration>   
 
 <!-- reducer 获取数据的方式 -->  <property>      
 <name>yarn.nodemanager.aux-services</name>      
 <value>mapreduce_shuffle</value>    
 </property>   
 
 <!-- 指定 YARN 的 ResourceManager 的地址 -->    
 <property>     
 <name>yarn.resourcemanager.hostname</name>     
 <value>hadoop102</value>    
 </property>   
 </configuration> 
```
  

 （4）mapreduce   
  mapred-env.sh    
[atguigu@hadoop102 hadoop]$ vi mapred-env.sh   
export JAVA_HOME=/opt/module/jdk1.8.0_144   
  mapred-site.xml   
[atguigu@hadoop102 hadoop]$ vi mapred-site.xml   
<configuration>    
<!-- 指定 mr 运行在 yarn 上 -->    
<property>     
<name>mapreduce.framework.name</name>  
```
 

  
### 集群启动及测试     
1）启动集群     
如果集群是第一次启动，需要格式化namenode    
[atguigu@hadoop101 hadoop-2.7.2]$ bin/hdfs namenode -format     
   
（1）启动 HDFS：     
[root@hadoop101 hadoop-2.7.2]$ sbin/start-dfs.sh     
[root@hadoop101 hadoop-2.7.2]$ jps     
4166 NameNode     
4482 Jps     
4263 DataNode     
 
[root@hadoop102 hadoop-2.7.2]$ jps     
3218 DataNode     
3288 Jps     
 
[root@hadoop103 hadoop-2.7.2]$ jps     
3221 DataNode       
3283 SecondaryNameNode     
3364 Jps   

（2）启动 yarn   
[root@hadoop102 hadoop-2.7.2]$ sbin/start-yarn.sh   
注意：Namenode 和 ResourceManger 如果不是同一台机器，不能在 NameNode 上启动 yarn，应该在 ResouceManager 所在的机器上启动 yarn  

# Zookeeper分布式集群部署  
Zookeeper是一个开源分布式的，为分布式应用提供协调服务的Apache项目  
Zookeeper从设计模式角度来理解：是一个基于观 察者模式设计的分布式服务管理框架，它负责存 储和管理大家都关心的数据，然后接受观察者的 注册，一旦这些数据的状态发生变化，Zookeeper 就将负责通知已经在Zookeeper上注册的那些观察 者 做出 相应 的反 应 ， 从而 实现 集群 中类似 Master/Slave管理模式
 分布式安装部署   
0）集群规划   
在 hadoop101、hadoop102 和 hadoop103 三个节点上部署 Zookeeper。 
1）解压安装  
（1）解压 zookeeper 安装包到/opt/module/目录下  
 [root@hadoop101 software]$ tar -zxvf zookeeper-3.4.10.tar.gz -C /opt/module/   

（2）在/opt/module/zookeeper-3.4.10/这个目录下创建 zkData   
 mkdir -p zkData   

（3）重命名/opt/module/zookeeper-3.4.10/conf 这个目录下的 zoo_sample.cfg 为 zoo.cfg    
 mv zoo_sample.cfg zoo.cfg   

2）配置 zoo.cfg 文件   
 （1）具体配置 
 dataDir=/opt/module/zookeeper-3.4.10/zkData   
 增加如下配置   
 #######################cluster##########################   
server.1=hadoop101:2888:3888   
server.2=hadoop102:2888:3888   
server.3=hadoop103:2888:3888   
  
（2）配置参数解读   
Server.A=B:C:D。   
A 是一个数字，表示这个是第几号服务器；   
B 是这个服务器的 ip 地址；   
C 是这个服务器与集群中的 Leader 服务器交换信息的端口； 
D 是万一集群中的 Leader 服务器挂了，需要一个端口来重新进行选举，选出一个新的  
Leader，而这个端口就是用来执行选举时服务器相互通信的端口。    
集群模式下配置一个文件myid，这个文件在dataDir目录下，这个文件里面有一个数据  
就是 A 的值，Zookeeper 启动时读取此文件，拿到里面的数据与 zoo.cfg 里面的配置信息比  

较从而判断到底是哪个 server。   
3）集群操作   
（1）在/opt/module/zookeeper-3.4.10/zkData 目录下创建一个 myid 的文件   
 touch myid   

添加 myid 文件，注意一定要在 linux 里面创建，在 notepad++里面很可能乱码   
（2）编辑 myid 文件   
 vi myid   

 在文件中添加与 server 对应的编号：如 1  
（3）拷贝配置好的 zookeeper 到其他机器上   
 scp -r zookeeper-3.4.10/ root@hadoop102.atguigu.com:/opt/app/   
 scp -r zookeeper-3.4.10/ root@hadoop103.atguigu.com:/opt/app/   

 并分别修改 myid 文件中内容为 2、3   
（4）分别启动 zookeeper   
 [root@hadoop101 zookeeper-3.4.10]# bin/zkServer.sh start   
[root@hadoop102 zookeeper-3.4.10]# bin/zkServer.sh start   
[root@hadoop103 zookeeper-3.4.10]# bin/zkServer.sh start   

（5）查看状态   
[root@hadoop101 zookeeper-3.4.10]# bin/zkServer.sh status   
JMX enabled by default   
Using config: /opt/module/zookeeper-3.4.10/bin/../conf/zoo.cfg   
Mode: follower   
[root@hadoop102 zookeeper-3.4.10]# bin/zkServer.sh status   
JMX enabled by default   
Using config: /opt/module/zookeeper-3.4.10/bin/../conf/zoo.cfg   
Mode: leader   
[root@hadoop103 zookeeper-3.4.5]# bin/zkServer.sh status   
JMX enabled by default 
Using config: /opt/module/zookeeper-3.4.10/bin/../conf/zoo.cfg   
Mode: follower   

# HBase  
HBase 的原型是 Google 的 BigTable 论文，受到了该论文思想的启发，目前作为 Hadoop 的子项目来开发维护，用于支持结构化的数据存储   
HBase 一种是作为存储的分布式文件系统，另一种是作为数据处理模型的 MR 框架。因为日常开发人员比较熟练的是结构化的数据进行处理，但是在 HDFS 直接存储的文件往往不具有结构化，所以催生出了 HBase 在 HDFS 上的操作。如果需要查询数据，只需要通过键值便可以成功访问。 
HBase 部署与使用   
首先保证 Zookeeper 集群的正常部署，并启动之：   
 ~/modules/zookeeper-3.4.5/bin/zkServer.sh start   

Hadoop 集群的正常部署并启动：   
 ~/modules/hadoop-2.7.2/sbin/start-dfs.sh   
$ ~/modules/hadoop-2.7.2/sbin/start-yarn.sh   

HBase 的解压   
tar -zxf ~/softwares/installations/hbase-1.3.1-bin.tar.gz -C ~/modules/   

HBase 的配置文件   
需要修改 HBase 对应的配置文件。   
hbase-env.sh 修改内容：   
export JAVA_HOME=/home/admin/modules/jdk1.8.0_121   
export HBASE_MANAGES_ZK=false   

hbase-site.xml 修改内容：  
 <configuration>    
 <property>           
 <name>hbase.rootdir</name>          
 <value>hdfs://hadoop101:9000/hbase</value>      
 </property>  
 
 <property>       
 <name>hbase.cluster.distributed</name>     
 <value>true</value>   
 </property>   
 
   <!-- 0.98 后的新变动，之前版本没有.port,默认端口为 60000 -->   
   <property>    
   <name>hbase.master.port</name>    
   <value>16000</value>   
   </property>  
 
 <property>       
 <name>hbase.zookeeper.quorum</name>    
 <value>hadoop101:2181,hadoop102:2181,hadoop103:2181</value>  </property> 
 
 <property>      
 <name>hbase.zookeeper.property.dataDir</name>   
 <value>/home/admin/modules/zookeeper-3.4.5/zkData</value>  
 </property>  
 </configuration>  

regionservers： 
hadoop101  
hadoop102  
hadoop103  

HBase 需要依赖的 Jar 包   
由于 HBase 需要依赖 Hadoop，所以替换 HBase 的 lib 目录下的 jar 包，以解决兼容问题  
1) 删除原有的 jar  
$ rm -rf /home/admin/modules/hbase-1.3.1/lib/hadoop-*   
$ rm -rf /home/admin/modules/hbase-1.3.1/lib/zookeeper-3.4.6.jar   
 
2) 拷贝新 jar，涉及的 jar 有  
hadoop-annotations-2.7.2.jar   
hadoop-auth-2.7.2.jar   
hadoop-client-2.7.2.jar   
hadoop-common-2.7.2.jar   
hadoop-hdfs-2.7.2.jar   
hadoop-mapreduce-client-app-2.7.2.jar   
hadoop-mapreduce-client-common-2.7.2.jar   
hadoop-mapreduce-client-core-2.7.2.jar    
hadoop-mapreduce-client-hs-2.7.2.jar 
hadoop-mapreduce-client-hs-plugins-2.7.2.jar   
hadoop-mapreduce-client-jobclient-2.7.2.jar  
hadoop-mapreduce-client-jobclient-2.7.2-tests.jar   
hadoop-mapreduce-client-shuffle-2.7.2.jar   
hadoop-yarn-api-2.7.2.jar   
hadoop-yarn-applications-distributedshell-2.7.2.jar   
hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar   
hadoop-yarn-client-2.7.2.jar   
hadoop-yarn-common-2.7.2.jar   
hadoop-yarn-server-applicationhistoryservice-2.7.2.jar   
hadoop-yarn-server-common-2.7.2.jar   
hadoop-yarn-server-nodemanager-2.7.2.jar   
hadoop-yarn-server-resourcemanager-2.7.2.jar   
hadoop-yarn-server-tests-2.7.2.jar   
hadoop-yarn-server-web-proxy-2.7.2.jar   
zookeeper-3.4.5.jar   
 
提示：这些 jar 包的对应版本应替换成你目前使用的 hadoop 版本，具体情况具体分析。  
 查找 jar 包举例  
find /home/admin/modules/hadoop-2.7.2/ -name hadoop-annotations*   

然后将找到的 jar 包复制到 HBase 的 lib 目录下即可。  
HBase 软连接 Hadoop 配置   
$ ln -s ~/modules/hadoop-2.7.2/etc/hadoop/core-site.xml   
~/modules/hbase-1.3.1/conf/core-site.xml   
$ ln -s ~/modules/hadoop-2.7.2/etc/hadoop/hdfs-site.xml   
~/modules/hbase-1.3.1/conf/hdfs-site.xml   

HBase 远程 scp 到其他集群   
$ scp -r /home/admin/modules/hbase-1.3.1/ hadoop102:/home/admin/modules/   
$ scp -r /home/admin/modules/hbase-1.3.1/ hadoop103:/home/admin/modules/   

HBase 服务的启动  
启动方式 1   
$ bin/hbase-daemon.sh start master   
$ bin/hbase-daemon.sh start regionserver   

提示：如果集群之间的节点时间不同步，会导致 regionserver 无法启动，抛出  
ClockOutOfSyncException 异常。   
修复提示：   
a、同步时间服务   

启动方式 2：  
$ bin/start-hbase.sh  
 
对应的停止服务：  
$ bin/stop-hbase.sh  

提示：如果使用的是 JDK8 以 上 版 本 ， 则 应 在 hbase-evn.sh 中 移除  
“HBASE_MASTER_OPTS”和“HBASE_REGIONSERVER_OPTS”配置。   
查看 Hbse 页面   
http://hadoop101:16010    

# Kafka  
在流式计算中，Kafka一般用来缓存数据，Spark通过消费Kafka的数据进行计算。  
1）Apache Kafka是一个开源消息系统，由Scala写成。是由Apache软件基金会开发的一个开源消息系统项目。  
2）Kafka最初是由LinkedIn公司开发，并于    2011年初开源。2012年10月从Apache Incubator毕业。该项目的目标是为处理实时数据提供一个统一、高通量、低等待的平台。  
3）Kafka是一个分布式消息队列。Kafka对消息保存时根据Topic进行归类，发送消息者称为Producer，消息接受者称为Consumer，此外kafka集群有多个kafka实例组成，每个实例(server)成为broker。  
4）无论是kafka集群，还是producer和consumer都依赖于zookeeper集群保存一些meta信息，来保证系统可用性。  
Kafka集群部署  
下载地址：  
http://kafka.apache.org/downloads.html  

1）解压安装包  
[root@hadoop102 software]$ tar -zxvf kafka_2.11-0.9.0.0.tgz -C /opt/module/  
  
2）修改解压后的文件名称  
[root@hadoop101 module]$ mv kafka_2.11-0.11.0.0/ kafka  
3）在/opt/module/kafka目录下创建logs文件夹  
[root@hadoop101 kafka]$ mkdir logs  
4）修改配置文件  
[root@hadoop101 kafka]$ cd config/  
[root@hadoop101 config]$ vi server.properties  
输入以下内容：  
#broker的全局唯一编号，不能重复broker.id=0#是否允许删除topicdelete.topic.enable=true#处理网络请求的线程数量num.network.threads=3#用来处理磁盘IO的线程数量num.io.threads=8#发送套接字的缓冲区大小socket.send.buffer.bytes=102400#接收套接字的缓冲区大小socket.receive.buffer.bytes=102400#请求套接字的最大缓冲区大小socket.request.max.bytes=104857600#kafka运行日志存放的路径log.dirs=/opt/module/kafka/logs#topic在当前broker上的分区个数num.partitions=1#用来恢复和清理data下数据的线程数量num.recovery.threads.per.data.dir=1#segment文件保留的最长时间，超时将被删除log.retention.hours=168#配置连接Zookeeper集群地址zookeeper.connect=hadoop102:2181,hadoop103:2181,hadoop104:2181
5）配置环境变量
[root@hadoop101 module]# vi /etc/profile  


#KAFKA_HOMEexport KAFKA_HOME=/opt/module/kafkaexport PATH=$PATH:$KAFKA_HOME/bin  

[root@hadoop101 module]# source /etc/profile  

6）分发安装包  
[root@hadoop101 etc]# xsync profile  
[root@hadoop101 module]$ xsync kafka/  
 
7）分别在hadoop102和hadoop103上修改配置文件/opt/module/kafka/config/server.properties中的broker.id=1、broker.id=2  
       注：broker.id不得重复  
8）启动集群  
依次在hadoop101、hadoop102、hadoop103节点上启动kafka  
[root@hadoop101 kafka]$ bin/kafka-server-start.sh config/server.properties &  
[root@hadoop102 kafka]$ bin/kafka-server-start.sh config/server.properties &   
[root@hadoop103 kafka]$ bin/kafka-server-start.sh config/server.properties &  
 
9）关闭集群  
[root@hadoop102 kafka]$ bin/kafka-server-stop.sh stop  
[root@hadoop103 kafka]$ bin/kafka-server-stop.sh stop  
[root@hadoop104 kafka]$ bin/kafka-server-stop.sh stop  
 
# Flume  
Flume是Cloudera提供的一个高可用的，高可靠的，分布式的海量日志采集、聚合和传输的系统，Flume支持在日志系统中定制各类数据发送方，用于收集数据；同时，Flume提供对数据进行简单处理，并写到各种数据接受方（可定制）的能力。
1、解压Flume  
tar -zxf apache-flume-1.7.0-bin.tar.gz -C /opt/modules/  
vi flume-env.sh  
配置下环境变量问题  
export JAVA_HOME=/opt/modules/jdk1.8.0_191  
export HADOOP_HOME=/opt/modules/hadoop-2.6.0  
export HBASE_HOME=/opt/modules/hbase-1.0.0-cdh5.4.0  

将flume分发到其他两个节点  
scp -r flume-1.7.0-bin hadoop102:/opt/modules/  
scp -r flume-1.7.0-bin hadoop103:/opt/modules/  

对于结点2服务配置：  
vi flume-conf.properties  

agent2.sources = r1  
agent2.channels = c1   
agent2.sinks = k1  

agent2.sources.r1.type = exec  
agent2.sources.r1.command = tail -F /opt/datas/weblog-flume.log  
agent2.sources.r1.channels = c1  

agent2.channels.c1.type = memory  
agent2.channels.c1.capacity = 10000  
agent2.channels.c1.transactionCapacity = 10000  
agent2.channels.c1.keep-alive = 5  

agent2.sinks.k1.type = avro  
agent2.sinks.k1.channel = c1  
agent2.sinks.k1.hostname = hadoop101  
agent2.sinks.k1.port = 5555  
 
对于结点3配置  
vi flume-conf.properties  

agent3.sources = r1  
agent3.channels = c1  
agent3.sinks = k1  
  
agent3.sources.r1.type = exec  
agent3.sources.r1.command = tail -F /opt/datas/weblog-flume.log  
agent3.sources.r1.channels = c1  

agent3.channels.c1.type = memory  
agent3.channels.c1.capacity = 10000  
agent3.channels.c1.transactionCapacity = 10000  
agent3.channels.c1.keep-alive = 5  

agent3.sinks.k1.type = avro  
agent3.sinks.k1.channel = c1  
agent3.sinks.k1.hostname = hadoop101  
agent3.sinks.k1.port = 5555  

# Flume源码修改与HBase+Kafka集成  
如何修改flume源码因为我们需要在节点1上将flume同时发送至HBase以及Kafka，但是hbase结构需要自定义，所以有flume发送至hbase代码需要修改。  
步骤：  
1.下载Flume源码并导入Idea开发工具  
将apache-flume-1.7.0-src.tar.gz源码 下载到本地解压  

2）通过idea导入flume源码  
打开idea开发工具，选择File——》Open，找到源码包，选中flume-ng-hbase-sink，点击ok加载相应模块的源码。  
2、自己写个类完成类的修改。KfkAsyncHbaseEventSerializer这个是我自定义的。修改其中的下面这个方法。  
@Override  
    public List<PutRequest> getActions() {  
        List<PutRequest> actions = new ArrayList<>();  
        if (payloadColumn != null) {  
            byte[] rowKey;  
            try {  
                /*---------------------------代码修改开始---------------------------------*/  
                //解析列字段  
                String[] columns = new String(this.payloadColumn).split(",");  
                //解析flume采集过来的每行的值  
                String[] values = new String(this.payload).split(",");  
                for(int i=0;i < columns.length;i++) {  
                    byte[] colColumn = columns[i].getBytes();  
                    byte[] colValue = values[i].getBytes(Charsets.UTF_8);  

                    //数据校验：字段和值是否对应  
                    if (colColumn.length != colValue.length) break;  

                    //时间  
                    String datetime = values[0].toString();  
                    //用户id  
                    String userid = values[1].toString();  
                    //根据业务自定义Rowkey  
                    rowKey = SimpleRowKeyGenerator.getKfkRowKey(userid, datetime);  
                    //插入数据  
                    PutRequest putRequest = new PutRequest(table, rowKey, cf,  
                            colColumn, colValue);  
                    actions.add(putRequest);  
                    /*---------------------------代码修改结束---------------------------------*/
                }  
            } catch (Exception e) {  
                throw new FlumeException("Could not get row key!", e);  
            }  
        }  
        return actions;  
    }  

修改这个类中自定义KEY生成方法  
public class SimpleRowKeyGenerator {  

  public static byte[] getKfkRowKey(String userid,String datetime)throws UnsupportedEncodingException {  
    return (userid + datetime + String.valueOf(System.currentTimeMillis())).getBytes("UTF8");  
  }  
}   

3、应该进行测试，但是这边测试完成，目前不知如何搭建，就直接生成jar包放到虚拟机直接用了。  
4、生成jar包，idea很好用  
可参考：https://jingyan.baidu.com/article/c275f6ba0bbb65e33d7567cb.html  
1）在idea工具中，选择File——》ProjectStructrue  
2）左侧选中Artifacts，然后点击右侧的+号，最后选择JAR——》From modules with dependencies  
3）一定要设置main class这一项选择自己要打包的类，然后直接点击ok  
4）删除其他依赖包，只把flume-ng-hbase-sink打成jar包就可以了。  
5）然后依次点击apply，ok  
6）点击build进行编译，会自动打成jar包  
7）到项目的apache-flume-1.7.0-src\flume-ng-sinks\flume-ng-hbase-sink\classes\artifacts\flume_ng_hbase_sink_jar目录下找到刚刚打的jar包  
8）将打包名字替换为flume自带的包名flume-ng-hbase-sink-1.7.0.jar ，然后上传至虚拟机上flume/lib目录下，覆盖原有的jar包即可。  
修改flume配置  
这里在节点1上修改flume的配置，完成与hbase和kafka的集成。（flume自定义的jar已经上传覆盖）  
修改flume-conf.properties  
agent1.sources = r1  
agent1.channels = kafkaC hbaseC   
agent1.sinks =  kafkaSink hbaseSink  

agent1.sources.r1.type = avro  
agent1.sources.r1.channels = hbaseC kafkaC  
agent1.sources.r1.bind = bigdata-pro01.kfk.com  
agent1.sources.r1.port = 5555  
agent1.sources.r1.threads = 5  
# flume-hbase  
agent1.channels.hbaseC.type = memory  
agent1.channels.hbaseC.capacity = 100000  
agent1.channels.hbaseC.transactionCapacity = 100000  
agent1.channels.hbaseC.keep-alive = 20  

agent1.sinks.hbaseSink.type = asynchbase   
agent1.sinks.hbaseSink.table = weblogs  
agent1.sinks.hbaseSink.columnFamily = info  
agent1.sinks.hbaseSink.channel = hbaseC  
agent1.sinks.hbaseSink.serializer = org.apache.flume.sink.hbase.KfkAsyncHbaseEventSerializer  
agent1.sinks.hbaseSink.serializer.payloadColumn = datatime,userid,searchname,retorder,cliorder,cliurl  
#flume-kafka  
agent1.channels.kafkaC.type = memory  
agent1.channels.kafkaC.capacity = 100000  
agent1.channels.kafkaC.transactionCapacity = 100000  
agent1.channels.kafkaC.keep-alive = 20  

agent1.sinks.kafkaSink.channel = kafkaC  
agent1.sinks.kafkaSink.type = org.apache.flume.sink.kafka.KafkaSink  
agent1.sinks.kafkaSink.brokerList = bigdata-pro01.kfk.com:9092,bigdata-pro02.kfk.com:9092,bigdata-pro03.kfk.com:9092  
agent1.sinks.kafkaSink.topic = weblogs  
agent1.sinks.kafkaSink.zookeeperConnect = bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181  
agent1.sinks.kafkaSink.requiredAcks = 1  
agent1.sinks.kafkaSink.batchSize = 1  
agent1.sinks.kafkaSink.serializer.class = kafka.serializer.StringEncoder  

项目进行到这里，已经完成了节点2和节点3上flume采集配置、节点1上flume采集并发送至kafka和hbase配置。  
# Flume+HBase+Kafka集成全流程测试  
全流程测试简介  
将完成对前面所有的设计进行测试，核心是进行flume日志的采集、汇总以及发送至kafka消费、hbase保存。  
原始数据的设计：  
下载搜狗实验室数据  
http://www.sogou.com/labs/resource/q.php  
数据格式为:访问时间\t用户ID\t[查询词]\t该URL在返回结果中的排名\t用户点击的顺序号\t用户点击的URL  
3、日志简单处理  
1）将文件中的tab更换成逗号  
cat weblog.log|tr “\t” “,” > weblog2.log  
2）将文件中的空格更换成逗号  
cat weblog2.log|tr “ “ “,” > weblog3.log  
处理完：  

编写模拟日志生成过程  
1、代码实现  
实现功能是将原始日志，每次读取一行不断写入到另一个文件中（weblog-flume.log），所以这个文件就相等于服务器中日志不断增加的过程。编写完程序，将该项目打成weblogs.jar包，然后上传至bigdata-pro02.kfk.com节点和bigdata-pro03.kfk.com节点的/opt/jars目录下（目录需要提前创建）
代码工程地址：https://github.com/changeforeda/Big-Data-Project/tree/master/code/DataProducer  
2、编写运行模拟日志程序的shell脚本  
1）
在bigdata-pro02.kfk.com节点的/opt/datas目录下，创建weblog-shell.sh脚本。  
vi weblog-shell.sh  
#/bin/bash  
echo "start log......"  
#第一个参数是原日志文件，第二个参数是日志生成输出文件  
java -jar /opt/jars/weblogs.jar /opt/datas/weblog.log /opt/datas/weblog-flume.log  

修改weblog-shell.sh可执行权限  
chmod 777 weblog-shell.sh  
2）  
将bigdata-pro02.kfk.com节点上的/opt/datas/目录拷贝到bigdata-pro03节点.kfk.com  
scp -r /opt/datas/ bigdata-pro03.kfk.com:/opt/datas/  

3、运行测试  
/opt/datas/weblog-shell.sh  

编写一些shell脚本便于执行  
1、编写启动flume服务程序的shell脚本  
1.在bigdata-pro02.kfk.com节点的flume安装目录下编写flume启动脚本。  
vi flume-kfk-start.sh  
#/bin/bash  
echo "flume-2 start ......"  
bin/flume-ng agent --conf conf -f conf/flume-conf.properties -n agent2 -Dflume.root.logger=INFO,console  
2.在bigdata-pro03.kfk.com节点的flume安装目录下编写flume启动脚本。  
vi flume-kfk-start.sh    
#/bin/bash
echo "flume-3 start ......"  
bin/flume-ng agent --conf conf -f conf/flume-conf.properties -n agent3 -Dflume.root.logger=INFO,console  
3.在bigdata-pro01.kfk.com节点的flume安装目录下编写flume启动脚本。  
vi flume-kfk-start.sh  
#/bin/bash  
echo "flume-1 start ......"  
bin/flume-ng agent --conf conf -f conf/flume-conf.properties -n agent1 -Dflume.root.logger=INFO,console  

2、编写Kafka Consumer执行脚本  
1.在bigdata-pro01.kfk.com节点的Kafka安装目录下编写Kafka Consumer执行脚本  
vi kfk-test-consumer.sh  
#/bin/bash  
echo "kfk-kafka-consumer.sh start ......"  
bin/kafka-console-consumer.sh --zookeeper bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181 --from-beginning --topic weblogs
2.将kfk-test-consumer.sh脚本分发另外两个节点  
scp kfk-test-consumer.sh bigdata-pro02.kfk.com:/opt/modules/kakfa_2.11-0.8.2.1/  
scp kfk-test-consumer.sh bigdata-pro03.kfk.com:/opt/modules/kakfa_2.11-0.8.2.1/  

联调测试-数据采集分发  
1、在各个节点上启动zk   
/opt/modules/zookeeper-3.4.5-cdh5.10.0/sbin/zkServer.sh start    
/opt/modules/zookeeper-3.4.5-cdh5.10.0/bin/zkCli.sh  登陆客户端进行测试是否启动成功  

2、启动hdfs  --- http://bigdata-pro01.kfk.com:50070/  
在节点1：/opt/modules/hadoop-2.6.0/sbin/start-dfs.sh   
#节点1 和 节点2  启动namenode高可用  
/opt/modules/hadoop-2.6.0/sbin/hadoop-daemon.sh start zkfc  

3、启动hbase  ----http://bigdata-pro01.kfk.com:60010/ 
#节点 1  启动hbase  
/opt/modules/hbase-1.0.0-cdh5.4.0/bin/start-hbase.sh  
#在节点2 启动备用master  
/opt/modules/hbase-1.0.0-cdh5.4.0/bin/hbase-daemon.sh start  master  
#启动hbase的shell用于操作  
/opt/modules/hbase-1.0.0-cdh5.4.0/bin/hbase shell  
#创建hbase业务表  
bin/hbase shell  
create 'weblogs','info'  
 
4、启动kafka  
#在各个个节点启动kafka  
cd /opt/modules/kafka_2.10-0.9.0.0  
bin/kafka-server-start.sh config/server.properties &  
#创建业务  
bin/kafka-topics.sh --zookeeper bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181 --create --topic weblogs --replication-factor 2 --partitions 1
#消费(之前编写的脚本可以用)  
bin/kafka-console-consumer.sh --zookeeper bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181 --from-beginning --topic weblogs

一定确保上述都启动成功能，利用jps查看各个节点进程情况。  
5、各个节点启动flume  
#三节点启动flume  
/opt/modules/flume-1.7.0-bin/flume-kfk-start.sh  
    
6、在节点2和3启动日志模拟生产  
/opt/datas/weblog-shell.sh  

7、启动kafka消费程序  
#消费（或者使用写好的脚本kfk-test-consumer.sh）  
bin/kafka-console-consumer.sh --zookeeper bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181 --from-beginning --topic weblogs

8、查看hbase数据写入情况  
./hbase-shell  
count 'weblogs'  

结果：  
kafka不断消费  

hbase数据不断增加  

mysql、Hive安装与集成  
为什么要用mysql?  
一方面，本项目用来存储Hive的元数据；另一方面，可以把离线分析结果放入mysql中；  
安装mysql  
通过yum在线mysql，具体操作命令如下所示(关于yum源可以修改为阿里的，比较快和稳定)
12345678910111213141516171819202122 1、在线安装mysql通过yum在线mysql，具体操作命令如下所示。yum clean allyum install mysql-server2、mysql 服务启动并测试sudo chown -R kfk:kfk /usr/bin/mysql    修改权限给kfk1）查看mysql服务状态sudo service mysqld status  2）启动mysql服务sudo service mysqld start3）设置mysql密码/usr/bin/mysqladmin -u root password '123456'4）连接mysqlmysql –uroot -p123456a）查看数据库show databases;mysqltestb）查看数据库use test;c）查看表列表show tables;
出现问题，大多数是权限问题，利用sudo执行或者重启mysql.  
# 安装Hive  
Hive在本项目中功能是，将hbase中的数据进行离线分析，输出处理结果，可以到mysql或者hbase，然后进行可视化。  
  

这里版本采用的是：apache-hive-2.1.0-bin.tar.gz  
（之前用apache-hive-0.13.1-bin.tar.gz出现和hbase集成失败，原因很奇怪，下一章详细讲）。  
1、解压  
123 步骤都老生常谈了。。。tar -zxf apache-hive-2.1.0-bin.tar.gz -C /opt/modules/mv  apache-hive-2.1.0-bin hive-2.1.0     //重命名  
2、修改配置文件  
12345678 1）hive-log4j.properties#日志目录需要提前创建hive.log.dir=/opt/modules/hive-2.1.0/logs2）修改hive-env.sh配置文件HADOOP_HOME=/opt/modules/hadoop-2.6.0HBASE_HOME=/opt/modules/hbase-1.0.0-cdh5.4.0# Hive Configuration Directory can be controlled by:export HIVE_CONF_DIR=/opt/modules/hive-2.1.0/conf
3、启动进行测试  
首先启动HDFS，然后创建Hive的目录  
bin/hdfs dfs -mkdir -p /tmp  
bin/hdfs dfs -chmod g+w /tmp  
bin/hdfs dfs -mkdir -p /user/hive/warehouse  
bin/hdfs dfs -chmod g+w /user/hive/warehouse  

4、测试  
1234567 ./hive#查看数据库show databases;#使用默认数据库use default;#查看表show tables;  
Hive与mysql集成  
利用mysql放Hive的元数据。  
1、在/opt/modules/hive-2.1.0/conf目录下创建hive-site.xml文件，配置mysql元数据库。  
12345678910111213141516171819202122232425262728 <?xml version="1.0"?><?xml-stylesheet type="text/xsl" href="configuration.xsl"?><configuration>  <property>    <name>javax.jdo.option.ConnectionURL</name>    <value>jdbc:mysql://bigdata-pro01.kfk.com/metastore?createDatabaseIfNotExist=true</value>  </property>  <property>    <name>javax.jdo.option.ConnectionDriverName</name>    <value>com.mysql.jdbc.Driver</value>  </property> <property>    <name>javax.jdo.option.ConnectionUserName</name>    <value>root</value>  </property>  <property>    <name>javax.jdo.option.ConnectionPassword</name>    <value>123456</value>  </property>  <property>    <name>hbase.zookeeper.quorum</name>   	<value>bigdata-pro01.kfk.com,bigdata-pro02.kfk.com,bigdata-pro03.kfk.com</value>  </property></configuration>
2、设置用户连接信息  
1）查看用户信息  
mysql -uroot -p123456  
show databases;  
use mysql;  
show tables;  
select User,Host,Password from user;  

2）更新用户信息  
update user set Host=’%’ where User = ‘root’ and Host=’localhost’  

3）删除用户信息  
delete from user where user=’root’ and host=’127.0.0.1’  
select User,Host,Password from user;  
delete from user where host=’localhost’;  

删除到只剩图中这一行数据  


4）刷新信息  
flush privileges;  
3.拷贝mysql驱动包到hive的lib目录下  
cp mysql-connector-java-5.1.35.jar /opt/modules/hive-2.1.0/lib/  

4.保证第三台集群到其他节点无秘钥登录  
Hive与mysql测试  
1.启动HDFS和YARN服务  
2.启动hive  
./hive  
3.通过hive服务创建表  
CREATE TABLE stu(id INT,name STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ‘\t’ ;  

4.创建数据文件  
vi /opt/datas/stu.txt  
00001 zhangsan  
00002 lisi  
00003 wangwu  
00004 zhaoliu  

5.加载数据到hive表中  
load data local inpath ‘/opt/datas/stu.txt’ into table stu;  
直接在hive查看表中内容就ok。  
在mysql数据库中hive的metastore元数据。（元数据是啥，去看看hive介绍吧）  

# Hive与Hbase集成  
Hive与HBase集成配置  
1、在hive-site.xml文件中配置Zookeeper，hive通过这个参数去连接HBase集群。  
123 <property>    <name>hbase.zookeeper.quorum</name>   <value>bigdata-pro01.kfk.com,bigdata-pro02.kfk.com,bigdata-pro03.kfk.com</value></property>
2、需要把hbase中的部分jar包拷贝到hive中  
这里采用软连接的方式：  
执行如下命令：  
12345678910111213141516171819 export HBASE_HOME=/opt/modules/hbase-1.0.0-cdh5.4.0export HIVE_HOME=/opt/modules/hive-2.1.0ln -s $HBASE_HOME/lib/hbase-server-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-server-1.0.0-cdh5.4.0.jarln -s $HBASE_HOME/lib/hbase-client-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-client-1.0.0-cdh5.4.0.jarln -s $HBASE_HOME/lib/hbase-protocol-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-protocol-1.0.0-cdh5.4.0.jar ln -s $HBASE_HOME/lib/hbase-it-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-it-1.0.0-cdh5.4.0.jar ln -s $HBASE_HOME/lib/htrace-core-3.0.4.jar $HIVE_HOME/lib/htrace-core-3.0.4.jarln -s $HBASE_HOME/lib/hbase-hadoop2-compat-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-hadoop2-compat-1.0.0-cdh5.4.0.jar ln -s $HBASE_HOME/lib/hbase-hadoop-compat-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-hadoop-compat-1.0.0-cdh5.4.0.jarln -s $HBASE_HOME/lib/high-scale-lib-1.1.1.jar $HIVE_HOME/lib/high-scale-lib-1.1.1.jar ln -s $HBASE_HOME/lib/hbase-common-1.0.0-cdh5.4.0.jar $HIVE_HOME/lib/hbase-common-1.0.0-cdh5.4.0.jar
3、测试  
在hbase中建立一个表，里面存有数据（实际底层就是在hdfs上），然后Hive创建一个表与HBase中的表建立联系。  
1）先在hbase建立一个表  
（不熟悉的，看指令https://www.cnblogs.com/cxzdy/p/5583239.html）  

 
2）启动hive,建立联系（之前要先启动mysql，因为元数据在里面）  
12345678 create external table t1(key int,name string,age string)  STORED BY  'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES("hbase.columns.mapping" = ":key,info:name,info:age") TBLPROPERTIES("hbase.table.name" = "t1");
3）hive结果  
执行 select * from t1;  


4、为项目中的weblogs建立联系  
之前我们把数据通过flume导入到hbase中了，所以同样我们在hive中建立联系，可以用hive对hbase中的数据进行简单的sql分析，离线分析。  
123456789101112 create external table weblogs(id string,datatime string,userid string,searchname string,retorder string,cliorder string,cliurl string)  STORED BY  'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES("hbase.columns.mapping" = ":key,info:datatime,info:userid,info:searchname,info:retorder,info:cliorder,info:cliurl") TBLPROPERTIES("hbase.table.name" = "weblogs");
Hive与HBase集成中的致命bug  
问题如图：  


参考办法：https://www.cnblogs.com/zlslch/p/8228781.html  
按照上述，参考还是解决不了。  
最初怀疑是hbase中的jar包没有导入到hive中，或者导入错了，结果不是这个原因。网上有个大哥也是遇到这个问题了，写了一篇日志，最后他说不知如何解决？？
--------------------------------------------------------------------------------
最终：我去官网看看，官网上说，hbase 1.x之后的版本，需要更高版本的hive匹配，最好是hive 2.x,上述的错误是因为我用的hive-0.13.1-bin和hbase-1.0.0-cdh5.4.0，应该是不兼容导致的，莫名bug。于是采用了 hive-2.1.0，我查了下这个版本与hadoop其他组件也是兼容的，所以，采用这个。配置仍然采用刚才的方法（上一章和这一章），主要有mysql元数据配置（驱动包别忘了），各种xml配置，测试下。最后，在重启hive之前，先把hbase重启了，很重要。终于成功了。。开心。
# 基于IDEA环境下的Spark2.X程序开发  
开发环境配置  
1、安装idea  
2、安装maven  
官网下载：apache-maven-3.6.0  
3、安装java8，并配置环境变量  
4、安装scala，直接从idea插件下载安装  
5、安装hadoop在Windows中的运行环境，并配置环境变量   
（软件下载链接：https://github.com/changeforeda/Big-Data-Project/blob/master/README.md）  
IDEA程序开发  
可以参考这个链接很全：https://blog.csdn.net/zkf541076398/article/details/79297820  
1、新建maven项目  
2、配置maven  
3、选择配置scala和java版本  
4、新建scala目录并设置为source(看图)  


5、编写pom.xml文件  
这里主要你需要什么就放什么，可以github上找例子  
https://github.com/apache/spark/blob/master/examples/pom.xml  
我的pom，我自己可以用  
1234567891011121314151617181920212223242526272829303132333435363738394041424344454647484950515253 <?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">  <modelVersion>4.0.0</modelVersion>  <packaging>war</packaging>  <name>TestSpark</name>  <groupId>com.kfk.spark</groupId>  <artifactId>TestSpark</artifactId>  <version>1.0-SNAPSHOT</version>  <properties>    <scala.version>2.11.12</scala.version>    <scala.binary.version>2.11</scala.binary.version>    <spark.version>2.2.0</spark.version>  </properties>  <dependencies>    <dependency>      <groupId>org.apache.spark</groupId>      <artifactId>spark-core_${scala.binary.version}</artifactId>      <version>${spark.version}</version>    </dependency>    <dependency>      <groupId>org.apache.spark</groupId>      <artifactId>spark-streaming_${scala.binary.version}</artifactId>      <version>${spark.version}</version>    </dependency>    <dependency>      <groupId>org.apache.spark</groupId>      <artifactId>spark-sql_${scala.binary.version}</artifactId>      <version>${spark.version}</version>    </dependency>    <dependency>      <groupId>org.apache.spark</groupId>      <artifactId>spark-hive_${scala.binary.version}</artifactId>      <version>${spark.version}</version>    </dependency>    <dependency>      <groupId>org.apache.spark</groupId>      <artifactId>spark-streaming-kafka-0-10_${scala.binary.version}</artifactId>      <version>${spark.version}</version>    </dependency>    <dependency>      <groupId>org.apache.hadoop</groupId>      <artifactId>hadoop-client</artifactId>      <version>2.6.0</version>    </dependency>  </dependencies></project>
6、编写测试程序  
1234567891011121314151617181920 import org.apache.spark.sql.SparkSessionobject test {  def main(args: Array[String]): Unit = {     val spark = SparkSession      .builder       .master("yarn-cluster")     //  .master("local[2]")      .appName("HdfsTest")      .getOrCreate()    val path = args(0)    val out = args(1)    val rdd = spark.sparkContext.textFile(path)    val lines = rdd.flatMap(_.split(" ")).map(x=>(x,1)).reduceByKey((a,b)=>(a+b)).saveAsTextFile(out)  }}
7、本地测试  
直接master(“local[2]”)，指定windows下的路径就可以了。如果不能运行一定是开发环境有问题，主要看看hadoop环境变量配置了吗  
8、打成jar包  
可参考：https://jingyan.baidu.com/article/c275f6ba0bbb65e33d7567cb.html  
9、上传至虚拟机中进行jar包方式提交到spark on yarn.  
运行底层还是依赖于hdfs，前提要启动zk /hadoop /yarn.  
1 bin/spark-submit --class  test  --master yarn --deploy-mode cluster /opt/jars/TestSpark.jar  hdfs://ns/input/stu.txt  hdfs://ns/out  
运行结束去，可以在yarn的web:http://bigdata-pro01.kfk.com:8088/cluster/  
看见调度success标志。   
 


10、如果运行失败怎么办？看日志  
有一个比较好的入口上图圈中的logs：  
先配置yarn-site.xml   
1234 <property>        
 <name>yarn.log.server.url</name>  
 <value>http://bigdata-pro01.kfk.com:19888/jobhistory/logs</value></property>
需要重启yarn，  

并在你配置节点启动历史服务器./mr-jobhistory-daemon.sh start historyserver  
点击：http://bigdata-pro01.kfk.com:8088/cluster  

# Spark Streaming实时数据处理  
Spark Streaming简介  
本质上就是利用批处理时间间隔来处理一小批的RDD集合。  

idea中程序测试读取socket  
1、在节点1启动nc  
nc -lk 9999  
输入一些单词  
2、在idea中运行程序  
12345678910111213141516171819 import org.apache.spark.SparkConfimport org.apache.spark.streaming.{Seconds, StreamingContext}object TestStreaming {  def main(args: Array[String]): Unit = {    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")    val ssc = new StreamingContext(conf, Seconds(5))    val lines = ssc.socketTextStream("bigdata-pro01.kfk.com",9999)    val words = lines.flatMap(_.split(" "))    //map reduce 计算    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)    wordCounts.print()    ssc.start()    ssc.awaitTermination()  }}

sparkstreaming和kafka进行集成  
版本问题：  


遇到了版本问题，之前用的是kafka0.9，现在和idea集成开发一般是kafka0.10了，还好官网里有支持kafka0.9程序案例，要不然就完犊子了，参考官网进行编写：  
http://spark.apache.org/docs/2.2.0/streaming-kafka-0-8-integration.html  
代码案例：https://github.com/apache/spark/blob/v2.2.0/examples/src/main/scala/org/apache/spark/examples/streaming/DirectKafkaWordCount.scala
基于kafka0.9的测试程序  
123456789101112131415161718192021222324252627282930313233 import kafka.serializer.StringDecoderimport org.apache.spark.sql.SparkSessionimport org.apache.spark.streaming.kafka.KafkaUtilsimport org.apache.spark.streaming.{Seconds, StreamingContext}object KfkStreaming {   def main(args: Array[String]): Unit = {     val spark  = SparkSession.builder()       .master("local[2]")       .appName("kfkstreaming").getOrCreate()     val sc =spark.sparkContext     val ssc = new StreamingContext(sc, Seconds(5))     val topicsSet = Set("weblogs")     val kafkaParams = Map[String, String]("metadata.broker.list" -> "bigdata-pro01.kfk.com:9092")     val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](       ssc, kafkaParams, topicsSet)     val lines = messages.map(_._2)     val words = lines.flatMap(_.split(" "))     val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)     wordCounts.print()     // Start the computation     ssc.start()     ssc.awaitTermination()   }}
在节点1上启动kafka程序  
12 bin/kafka-server-start.sh config/server.propertiesbin/kafka-console-producer.sh --broker-list bigdata-pro01.kfk.com:9092 --topic weblogs
运行结果：  

13. Structured Streaming业务数据实时分析  
1. Structured Streaming与kafka集成   
    1）Structured Streaming是Spark2.2.0新推出的，要求kafka的版本0.10.0及以上。集成时需将如下的包拷贝到Spark的jar包目录下。  

kafka_2.11-0.10.1.0.jar  
kafka-clients-0.10.1.0.jar  
spark-sql-kafka-0-10_2.11-2.2.0.jar  
spark-streaming-kafka-0-10_2.11-2.1.0.jar  

    2）与kafka集成代码  

val df = spark  
      .readStream  
      .format("kafka")  
      .option("kafka.bootstrap.servers", "node5:9092")  
      .option("subscribe", "weblogs")  
      .load()  
 
import spark.implicits._  
val lines = df.selectExpr("CAST(value AS STRING)").as[String]  

2. Structured Streaming与MySQL集成  
    1）mysql创建相应的数据库和数据表，用于接收数据  

create database test;  
use test;  
 
CREATE TABLE `webCount` (  
    `titleName` varchar(255) CHARACTER SET utf8 DEFAULT NULL,  
    `count` int(11) DEFAULT NULL  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;   
  
    2）与mysql集成代码  

val url ="jdbc:mysql://node5:3306/test"  
    val username="root"  
    val password="1234"  
 
    val writer = new JDBCSink(url,username,password)  
    val query = titleCount.writeStream  
      .foreach(writer)        
      .outputMode("update")  
      .trigger(ProcessingTime("5 seconds"))  
      .start()  

3. Structured Streaming向mysql数据库写入中文乱码解决  

    修改数据库文件my.cnf（linux下）  
 
[client]  
socket=/var/lib/mysql/mysql.sock    //添加  
default-character-set=utf8          //添加  
[mysqld]
character-set-server=utf8           //添加  
datadir=/var/lib/mysql  
socket=/var/lib/mysql/mysql.sock  
user=mysql  
# Disabling symbolic-links is recommended to prevent assorted security risks  
symbolic-links=0  
[mysqld_safe]  
log-error=/var/log/mysqld.log  
pid-file=/var/run/mysqld/mysqld.pid  

#  大数据Web可视化分析系统开发  
1. 基于业务需求的WEB系统设计（具体参照代码）  
 
2. 基于Echart框架的页面展示层开发   

    1）echart、JQuery下载  

    2）页面效果图选取及代码实现  

    

3. 工程编译并打包发布  

    参照之前将的idea打包方式，将spark web项目打包发布。  

4. 启动各个服务  

    1）启动zookeeper： zkServer.sh start  

    2）启动hadoop： start-all.sh  

    3）启动hbase： start-hbase  

    4）启动mysql： service mysqld start  

    5）node6（node7）启动flume： flume-kfk-start.sh，将数据发送到node5中  

    6）node5启动flume： flume-kfk-start.sh，将数据分别传到hbase和kafka中  

    7）启动kafka-0.10(最好三台都启动，不然易出错)：  

bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &   
    8）启动node6(node7)中的脚本：weblog-shell.sh  

    9）启动 StructuredStreamingKafka来从kafka中取得数据，处理后存到mysql中  

    10）启动web项目（sparkStu），该项目会从mysql数据库中读取数据展示到页面  





