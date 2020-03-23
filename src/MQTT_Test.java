import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class MQTT_Test{

    public static void main(String[] args) {

        String ADAFRUIT_USERNAME = "";
        String ADAFRUIT_AIO_KEY = "";

        try{
            BufferedReader reader = new BufferedReader(new FileReader("secrets.txt"));
            ADAFRUIT_USERNAME = reader.readLine().split("=")[1];
            ADAFRUIT_AIO_KEY = reader.readLine().split("=")[1];

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String topic = ADAFRUIT_USERNAME + "/feeds/sensorfeed";
        String msg_content = "Hello from java (not the island)!";
        int qos = 1; //QoS: 0 - at most once, 1 - at least once, 2 - exactly once
        String broker = "tcp://io.adafruit.com:1883"; //Adafruit IO broker
        String client_id = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient mqtt_client = new MqttClient(broker, client_id, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(ADAFRUIT_USERNAME);
            connOpts.setPassword(ADAFRUIT_AIO_KEY.toCharArray());
            System.out.println("Connecting to broker: " + broker);
            mqtt_client.connect(connOpts);
            System.out.println("Connected. Publishing message: " + msg_content);
            MqttMessage message = new MqttMessage(msg_content.getBytes());
            message.setQos(qos);
            mqtt_client.publish(topic, message);
            System.out.println("Message published");
            mqtt_client.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        }
        catch(MqttException me) {
            System.out.println("reason: " + me.getReasonCode());
            System.out.println("msg: " + me.getMessage());
            System.out.println("loc: " + me.getLocalizedMessage());
            System.out.println("cause: " + me.getCause());
            System.out.println("excep: " + me);
            me.printStackTrace();
        }
    }
}