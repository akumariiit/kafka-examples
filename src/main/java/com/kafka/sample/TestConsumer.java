package com.kafka.sample;

import com.expediagroup.event.userinteraction.UserInteraction;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

public class TestConsumer {

    private static final String TOPIC = "user_interaction_eg_domain_event_v1";

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) {
        final Properties props = new Properties();
//      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:9092");
//      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:8081");
      //    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:9092");
      //    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:8081");\
      // Prod
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1a-us-east-1.egdp-prod.aws.away.black:9092");
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1a-us-east-1.egdp-prod.aws.away.black:8081");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kira_consumer12");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        //props.put(ErrorHandlingDeserializer2.KEY_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        //props.put(ErrorHandlingDeserializer2.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (final KafkaConsumer<String, UserInteraction> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                try {
                    System.out.println("Reading...");
                    final ConsumerRecords<String, UserInteraction> records = consumer.poll(100);
                    System.out.println("Records size " + records.count());
                    for (final ConsumerRecord<String, UserInteraction> record : records) {
                        System.out.println(String.format("partition = %s and offset = %s", record.partition(), record.offset()));
                        String key;
                        UserInteraction value;
                        try {
                            key = record.key();
                        } catch (Exception e) {
                            System.out.println("Exception KEY");
                            break;
                        }
                        try {
                            value = record.value();
                        } catch (Exception e) {
                            System.out.println("Exception Value");
                            break;
                        }

                        if (value.getCommon() != null && value.getCommon().getPointOfSale() != null && value.getCommon().getPointOfSale().getBrand() !=null &&
                                !value.getCommon().getPointOfSale().getBrand().equalsIgnoreCase("hcom")) {
                            System.out.println(String.format("BEXXXXXXXXXXXXXXXXXXXXX"));
                            System.out.println(String.format("Key is = %s, brand is %s and full value is %s%n and RECORD::: %s",
                                    key, value.getCommon().getPointOfSale().getBrand(), value, record));
                        }
                        else {
                            System.out.println(String.format("EXPEDIAAAAAAAAAAAAA"));
                            System.out.println(String.format("Key is = %s, brand is %s and full value is %s%n and RECORD::: %s",
                                    key, value.getCommon().getPointOfSale().getBrand(), value, record));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception ***** " + e.getMessage());
                    break;
                }
            }
        }
    }
}
