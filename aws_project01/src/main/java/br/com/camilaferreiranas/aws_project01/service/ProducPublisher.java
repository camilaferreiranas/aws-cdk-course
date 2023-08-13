package br.com.camilaferreiranas.aws_project01.service;

import br.com.camilaferreiranas.aws_project01.enums.EventType;
import br.com.camilaferreiranas.aws_project01.model.Envelope;
import br.com.camilaferreiranas.aws_project01.model.Evento;
import br.com.camilaferreiranas.aws_project01.model.Product;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProducPublisher {

    private AmazonSNS snsClient;
    private Topic productEventsTopic;
    private ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(ProducPublisher.class);

    public ProducPublisher(AmazonSNS snsClient, @Qualifier("productEventsTopic") Topic productEventsTopic, ObjectMapper objectMapper) {
        this.snsClient =snsClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;

    }

    public void publishProductEvent(Product product, EventType eventType, String username) {
        Evento evento = new Evento();
        evento.setProductId(product.getId());
        evento.setCode(product.getCode());
        evento.setUsername(username);

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);

        try {
            envelope.setData(objectMapper.writeValueAsString(evento));

            snsClient.publish(
                    productEventsTopic.getTopicArn(),
                    objectMapper.writeValueAsString(envelope)
            );
        } catch (JsonProcessingException e) {
           LOG.error("Failed to create product event message");
        }
    }
}
