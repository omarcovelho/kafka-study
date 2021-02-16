/*
 * @(#)FavouriteColorApp.java 1.0 15/02/2021
 *
 * Copyright (c) 2021, Embraer. All rights reserved. Embraer S/A
 * proprietary/confidential. Use is subject to license terms.
 */
package br.com.omarcovelho.kafkastudy.favouritecolor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author marprado - Marco Prado
 * @version 1.0 15/02/2021
 */
public class FavouriteColorApp {

    public static void main(String[] args) {
        final Properties config = createConfig();

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> favouriteColorInput = builder.stream("favourite-color-input");

        favouriteColorInput
            .filter((key, value) -> value.contains(","))
            .selectKey((key, value) -> value.split(",")[0].toLowerCase())
            .mapValues((key, value) -> value.split(",")[1].toLowerCase())
            .filter((key, value) -> Arrays.asList("green", "blue", "red").contains(value))
            .to("user-keys-and-colors");

        KTable<String, String> favouriteColorsTable = builder.table("user-keys-and-colors");
        KTable<String, Long> favouriteColors = favouriteColorsTable
                .groupBy((key, value) -> new KeyValue<>(value, value))
                .count(Materialized.as("CountsByColors"));

        favouriteColors.toStream().to("favourite-color-output");

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), config);
        kafkaStreams.start();
        System.out.println(kafkaStreams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private static Properties createConfig() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-color-app");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return properties;
    }
}
