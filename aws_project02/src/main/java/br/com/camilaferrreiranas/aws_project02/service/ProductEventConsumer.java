package br.com.camilaferrreiranas.aws_project02.service;

import br.com.camilaferrreiranas.aws_project02.model.Envelope;
import br.com.camilaferrreiranas.aws_project02.model.ProductEvent;
import br.com.camilaferrreiranas.aws_project02.model.ProductEventLog;
import br.com.camilaferrreiranas.aws_project02.model.SnsMessage;
import br.com.camilaferrreiranas.aws_project02.repository.ProductEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(
            ProductEventConsumer.class
    );

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProductEventLogRepository productEventLogRepository;


    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws IOException, JMSException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);

        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);

        ProductEvent productEvent = objectMapper.readValue(
                envelope.getData(), ProductEvent.class
        );

        log.info("Product event received - Event: {} - Product: {}", envelope.getEventType(), productEvent.getProductId());
        ProductEventLog productEventLog = buildProductEventLog(envelope, productEvent);
        productEventLogRepository.save(productEventLog);

    }

    private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent) {
        long timestamp = Instant.now().toEpochMilli();
        ProductEventLog productEventLog = new ProductEventLog();
        productEventLog.setPk(productEvent.getCode());
        productEventLog.setSk(envelope.getEventType() + "_" + timestamp);
        productEventLog.setEventType(envelope.getEventType());
        productEventLog.setUsername(productEventLog.getUsername());
        productEventLog.setTimestamp(timestamp);
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond());
        return productEventLog;
    }
}
