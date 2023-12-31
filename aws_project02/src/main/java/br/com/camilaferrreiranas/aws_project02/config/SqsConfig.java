package br.com.camilaferrreiranas.aws_project02.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import jakarta.jms.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import software.amazon.awssdk.services.sqs.SqsClient;


@Configuration
@EnableJms
public class SqsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    private SQSConnectionFactory sqsConnectionFactory;


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        sqsConnectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                (SqsClient) AmazonSQSClientBuilder.standard()
                        .withRegion(awsRegion)
                        .withCredentials(new DefaultAWSCredentialsProviderChain())
                        .build()
        );

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsConnectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("2");//configuração de threads
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return factory;
    }


}
