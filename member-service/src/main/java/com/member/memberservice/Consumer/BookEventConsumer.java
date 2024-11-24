package com.member.memberservice.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookEventConsumer {

    @KafkaListener(topics = "book-events", groupId = "book-service-group")
    public void consumeBookEvent(String event) {
        System.out.println("Received event: " + event);
    }
}
