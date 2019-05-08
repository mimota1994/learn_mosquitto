package com.mqtt.test;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

 /**
  11  *
  12  * Title:Server Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
  13  *
  14  * @author yueli 2017年9月1日下午17:41:10
  15  */
public class ServerMQTT {
     // tcp://MQTT安装的服务器地址:MQTT定义的端口号
     public static final String HOST = "tcp://172.16.192.102:1883";
     // 定义一个主题
     public static final String TOPIC = "root/topic/123";
     // 定义MQTT的ID，可以在MQTT服务配置中指定
     private static final String clientid = "server11";

     private MqttClient client;
     private MqttTopic topic11;
     private String userName = "mosquitto";
     private String passWord = "mosquitto";

     private MqttMessage message;

     /**
 33      * 构造函数
 34      *
 35      * @throws MqttException
 36      */
     public ServerMQTT() throws MqttException {
         // MemoryPersistence设置clientid的保存形式，默认为以内存保存
         client = new MqttClient(HOST, clientid, new MemoryPersistence());
         connect();
     }

     /**
 44      * 用来连接服务器
 45      */
     private void connect() {
         MqttConnectOptions options = new MqttConnectOptions();
         options.setCleanSession(false);
         options.setUserName(userName);
         options.setPassword(passWord.toCharArray());
         // 设置超时时间
         options.setConnectionTimeout(10);
         // 设置会话心跳时间
         options.setKeepAliveInterval(20);
         try {
             client.setCallback(new PushCallback());
             client.connect(options);

             topic11 = client.getTopic(TOPIC);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     /**
 66      *
 67      * @param topic
 68      * @param message
 69      * @throws MqttPersistenceException
 70      * @throws MqttException
 71      */
     public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
         MqttDeliveryToken token = topic.publish(message);
         token.waitForCompletion();
         System.out.println("message is published completely! " + token.isComplete());
     }

     /**
 79      * 启动入口
 80      *
 81      * @param args
 82      * @throws MqttException
 83      */
     public static void main(String[] args) throws MqttException {
         ServerMQTT server = new ServerMQTT();

         server.message = new MqttMessage();
         server.message.setQos(1);
         server.message.setRetained(true);
         server.message.setPayload("hello,topic14".getBytes());
         server.publish(server.topic11, server.message);
         System.out.println(server.message.isRetained() + "------ratained状态");
     }
 }