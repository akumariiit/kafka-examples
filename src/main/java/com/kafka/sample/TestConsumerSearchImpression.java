package com.kafka.sample;

import com.expediagroup.event.search.impression.business.SearchImpressionEventKey;
import com.expediagroup.event.search.impression.business.SearchImpressionEventValue;
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

public class TestConsumerSearchImpression {

    private static final String TOPIC = "search_impression_eg_business_event_v1";

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) {
        final Properties props = new Properties();
        // test
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:9092");
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1b-us-east-1.egdp-test.aws.away.black:8081");
        // stage
        // props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:9092");
        // props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://kafka-1a-us-east-1.egdp-stage.aws.away.black:8081");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "connor_consumer_kush1");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        // props.put(ErrorHandlingDeserializer2.KEY_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        // props.put(ErrorHandlingDeserializer2.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (final KafkaConsumer<SearchImpressionEventKey, SearchImpressionEventValue> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                try {
                    System.out.println("Reading...");
                    final ConsumerRecords<SearchImpressionEventKey, SearchImpressionEventValue> records = consumer.poll(Duration.ofMillis(1000));
                    System.out.println("Records size " + records.count());
                    for (final ConsumerRecord<SearchImpressionEventKey, SearchImpressionEventValue> record : records) {
                        System.out.println(String.format("partition = %s and offset = %s", record.partition(), record.offset()));
                        SearchImpressionEventKey key;
                        SearchImpressionEventValue value;
                        try {
                            key = record.key();
                        } catch (Exception e) {
                            System.out.println("Exception KEY " + e.getMessage());
                            break;
                        }
                        try {
                            value = record.value();
                        } catch (Exception e) {
                            System.out.println("Exception Value " + e.getMessage());
                            break;
                        }
                        if (key.getTraceId().equalsIgnoreCase("zzzzzad99a7b4f6988e65a3c11111111")) {
                            System.out.println("GOT KEY FROM KUSHAGRAAAAAAAAAAAA ==================================");
                            System.out.println(String.format("Key is = %s, full value is %s%n and RECORD::: %s",
                                    key.getTraceId(), value, record));
                        }
//                            System.out.println(String.format("Key is = %s, full value is %s%n and RECORD::: %s",
//                                    key.getTraceId(), value, record));
                    }
                } catch (Exception e) {
                    System.out.println("Exception ***** " + e.getMessage());
                    break;
                }
            }
        }
    }
}
