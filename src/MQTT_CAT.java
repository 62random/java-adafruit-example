import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MQTT_CAT implements MqttCallback {

    public MQTT_CAT(){}

    public static void main(String[] args) throws Exception {
        new MQTT_CAT().test();
    }

    public void test() throws Exception {

        // Read credentials  from a file
        String ADAFRUIT_USERNAME = "";
        String ADAFRUIT_AIO_KEY = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("secrets.txt"));
            ADAFRUIT_USERNAME = reader.readLine().split("=")[1];
            ADAFRUIT_AIO_KEY = reader.readLine().split("=")[1];

        } catch (Exception e) {
            e.printStackTrace();
        }

        // parsing file "JSONExample.json"
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader("json.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // API MQtt
        String topic = ADAFRUIT_USERNAME + "/feeds/java-adafruit-example/json";
        int qos = 1;
        String broker = "tcp://io.adafruit.com:1883";
        String client_id = "java_client";
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqtt_client = new MqttClient(broker, client_id, persistence);
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(ADAFRUIT_USERNAME);
            connOpts.setPassword(ADAFRUIT_AIO_KEY.toCharArray());
            mqtt_client.connect(connOpts);
            mqtt_client.setCallback(this);
            mqtt_client.subscribe(topic);
        } catch (MqttException me) {
            me.printStackTrace();
        }


        // Ler linhas do terminal e mandá-las para o feed
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String message_string;
        while (true) {
            message_string = br.readLine();
            if (message_string == null)
                break;
            try {
                MqttMessage message = new MqttMessage(message_string.getBytes());
                message.setQos(qos);
                mqtt_client.publish(topic, message);
            } catch (MqttException me) {
                me.printStackTrace();
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println(mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}