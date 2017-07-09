package src;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Date 09/07/17
 * Time 4:37 PM
 *
 * @author yogin
 */
public class SharedSubCallbackRouter implements MqttCallback {
    private final Map<String, IMqttMessageListener> topicFilterListeners;

    public SharedSubCallbackRouter(Map<String, IMqttMessageListener> topicFilterListeners) {
        this.topicFilterListeners = topicFilterListeners;
    }

    public void addSubscriber(String topicFilter, IMqttMessageListener listener) {
        if (this.topicFilterListeners == null) {
             this.topicFilterListeners = new HashMap<>();
        }
        this.topicFilterListeners.put(topicFilter, listener);
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        for (Map.Entry<String, IMqttMessageListener> listenerEntry : topicFilterListeners.entrySet()) {
            String topicFilter = listenerEntry.getKey();
            if (isMatched(topicFilter, topic)) {
                listenerEntry.getValue().messageArrived(topic, message);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    /**
     * Paho topic matcher does not work with shared subscription topic filter of emqttd
     * https://github.com/eclipse/paho.mqtt.java/issues/367#issuecomment-300100385
     * <p>
     * http://emqtt.io/docs/v2/advanced.html#shared-subscription
     *
     * @param topicFilter the topicFilter for mqtt
     * @param topic       the topic
     * @return boolean for matched
     */
    private boolean isMatched(String topicFilter, String topic) {
        if (topicFilter.startsWith("$queue/")) {
            topicFilter = topicFilter.replaceFirst("\\$queue/", "");
        } else if (topicFilter.startsWith("$share/")) {
            topicFilter = topicFilter.replaceFirst("\\$share/", "");
            topicFilter = topicFilter.substring(topicFilter.indexOf('/'));
        }
        return MqttTopic.isMatched(topicFilter, topic);
    }
}
