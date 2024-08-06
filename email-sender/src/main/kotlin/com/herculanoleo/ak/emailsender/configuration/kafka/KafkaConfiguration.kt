package com.herculanoleo.ak.emailsender.configuration.kafka

import com.herculanoleo.ak.emailsender.models.constants.KAFKA_NOTIFICATION_RESULT_TOPIC
import com.herculanoleo.ak.emailsender.models.constants.KAFKA_SEND_EMAIL_TOPIC
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@EnableKafka
@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaProducerFactory(props: KafkaProperties): DefaultKafkaProducerFactory<String, String> {
        val producerProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to props.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        )
        return DefaultKafkaProducerFactory<String, String>(producerProps)
    }

    @Bean
    fun kafkaConsumerFactory(props: KafkaProperties): DefaultKafkaConsumerFactory<String, String> {
        val consumerProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to props.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to props.consumer.groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to props.consumer.autoOffsetReset
        )

        return DefaultKafkaConsumerFactory<String, String>(consumerProps)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>) = KafkaTemplate(producerFactory)

    @Bean(KAFKA_SEND_EMAIL_TOPIC)
    fun sendEmailTopic() = TopicBuilder
        .name(KAFKA_SEND_EMAIL_TOPIC)
        .partitions(3)
        .replicas(3)
        .build()

    @Bean(KAFKA_NOTIFICATION_RESULT_TOPIC)
    fun notificationResultTopic() = TopicBuilder
        .name(KAFKA_NOTIFICATION_RESULT_TOPIC)
        .partitions(3)
        .replicas(3)
        .build()

}
