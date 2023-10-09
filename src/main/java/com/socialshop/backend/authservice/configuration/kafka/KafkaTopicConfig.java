package com.socialshop.backend.authservice.configuration.kafka;

import com.socialshop.backend.authservice.constants.KafkaTopicConstant;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaTopicConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> ops = new HashMap<>();
        ops.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(ops);
    }

    @Bean
    public NewTopic topicEmailOtp() {
        return TopicBuilder.name(KafkaTopicConstant.emailOtpRegisterVerification).partitions(1).replicas(1).build();
    }

}
