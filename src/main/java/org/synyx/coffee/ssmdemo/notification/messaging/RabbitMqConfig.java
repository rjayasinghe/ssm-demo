package org.synyx.coffee.ssmdemo.notification.messaging;

import com.rabbitmq.client.AMQP;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;

import org.synyx.coffee.ssmdemo.notification.events.NoRouteForMessageEvent;
import org.synyx.coffee.ssmdemo.notification.events.PublishConfirmedEvent;
import org.synyx.coffee.ssmdemo.notification.events.PublishNotConfirmedEvent;

import java.util.Objects;


@Configuration
public class RabbitMqConfig {

    public static final String NOTIFICATION_EXCHANGE = "notifications";

    @Bean
    TopicExchange smsTopicExchange() {

        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }


    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, ApplicationEventPublisher publisher) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMandatory(true);
        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {

                @Override
                public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
                    String routingKey) {

                    if (replyCode == AMQP.NO_ROUTE) {
                        final String messageId = message.getMessageProperties().getMessageId();

                        if (StringUtils.hasText(messageId)) {
                            publisher.publishEvent(
                                new NoRouteForMessageEvent(message.getMessageProperties().getMessageId()));
                        }
                    }
                }
            });

        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

                @Override
                public void confirm(CorrelationData correlationData, boolean ack, String cause) {

                    if (Objects.isNull(correlationData)) {
                        return;
                    }

                    if (ack) {
                        publisher.publishEvent(new PublishConfirmedEvent(correlationData.getId()));
                    } else {
                        publisher.publishEvent(new PublishNotConfirmedEvent(correlationData.getId()));
                    }
                }
            });

        return template;
    }
}
