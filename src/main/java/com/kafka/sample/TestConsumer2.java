package com.kafka.sample;

import com.expedia.www.uis.prime.schema.UisPrimeBexDomainEventValue;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TestConsumer2 {

    private static final String TOPIC = "user_interactions_uis_prime_bex_domain_event_v1";

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) {
        final Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:9092");
//        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:8081");

         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:9092");
         props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:8081");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "hWireConsumer123111");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (final KafkaConsumer<String, UisPrimeBexDomainEventValue> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                try {
                    System.out.println("Reading...");
                    final ConsumerRecords<String, UisPrimeBexDomainEventValue> records = consumer.poll(Duration.ofMillis(1000));
                    System.out.println("Records size " + records.count());
                    for (final ConsumerRecord<String, UisPrimeBexDomainEventValue> record : records) {
                        System.out.println(String.format("partition = %s and offset = %s", record.partition(), record.offset()));
                        String key;
                        UisPrimeBexDomainEventValue value;
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
                        if (value.getDeviceCharacteristics() == null) {
                            System.out.println("OMGGGGGGG DC is null ******************************************** ");
                        }

                        if (value.getMetadata() != null && value.getMetadata().getName() != null) {
                            System.out.println(String.format("Key is = %s, full value is %s%n and RECORD::: %s",
                                    key, value, record));
                        }
//                            System.out.println(String.format("Key is = %s, full value is %s%n and RECORD::: %s",
//                                    key, value, record));
                    }
                } catch (Exception e) {
                    System.out.println("Exception ***** " + e.getMessage());
                    break;
                }
            }
        }
    }
}
