# paho-shared-sub-example
Example to handle callback for routing mqtt shared subscription in paho client

Paho's mqtt client library matches topic client side before delivering the messages to the subscribed listeners. The topic matching logic in mqttv3 does not incorporate the shared subscription topic filter.

Issue filed where callbacks not called for shared subscription filter: https://github.com/eclipse/paho.mqtt.java/issues/367

Here is the example for registering callback which adds custom router and not relying on Mqtt's callbacks map.

## Usage:
MqttClient has the support to add custom Callback for undelivered messages for unmatched topic filter subscribers.

```java
    MqttClient client = new MqttClient();
    MqttConnectOptions connOpts = new MqttConnectOptions(); //the connect opt
    client.connect(connOpts);

    String sharedTopic = "$shared/group1/test/topic";
    Map<String, IMqttMessageListener> listeners = new HashMap<>();
    listeners.put(sharedTopic, new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          //message received
        }
    });
    
    //Important to have `client.setCallback` before `client.subscribe`
    client.setCallback(new SharedSubCallbackRouter(listeners));
    client.subscribe(sharedTopic);
```

`SharedSubCallbackRouter` deals with the limitations of paho's `MqttTopic.isMatched(topicFilter, topic)` to match shared subscription topic filter's patterns.

This project describes `SharedSubCallbackRouter` source for reference. Test result and details on blog post: https://yogin16.github.io/2017/07/15/mqtt-shared-sub-with-paho/
