package com.enifl33fi.emailsender.service;

import com.enifl33fi.emailsender.dto.request.EmailRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class EmailRequestListener {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    @Qualifier("jmsConnectionFactory")
    @Autowired
    private ConnectionFactory connectionFactory;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @PostConstruct
    public void initialize() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue queue = session.createQueue("email.requests.queue");

            consumer = session.createConsumer(queue);

            consumer.setMessageListener(this::processMessage);

            connection.start();
            log.info("JMS listener started for queue: email.requests.queue");
        } catch (JMSException e) {
            log.error("JMS initialization failed", e);
            throw new RuntimeException("JMS initialization failed", e);
        }
    }

    private void processMessage(Message message) {
        try {
            if (message instanceof BytesMessage bytesMessage) {
                byte[] messageBytes = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(messageBytes);
                handleEmailRequest(messageBytes);
            } else {
                log.warn("Unsupported message type: {}", message.getClass().getName());
            }
        } catch (JMSException e) {
            log.error("Message processing error", e);
        }
    }

    private void handleEmailRequest(byte[] messageBytes) {
        try {
            EmailRequestDto request = objectMapper.readValue(messageBytes, EmailRequestDto.class);
            System.out.println(request.getEmail());
            System.out.println(request.getText());
            System.out.println(request.getSubject());
            emailService.sendEmail(request.getEmail(), request.getText(), request.getSubject());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse email request", e);
        } catch (Exception e) {
            log.error("Error processing email request", e);
        }
    }

    @PreDestroy
    public void cleanUp() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            log.info("JMS resources closed successfully");
        } catch (JMSException e) {
            log.error("Error closing JMS resources", e);
        }
    }
}
