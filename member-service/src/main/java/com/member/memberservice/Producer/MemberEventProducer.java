package com.member.memberservice.Producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.memberservice.Event.BookBorrowedEvent;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class MemberEventProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public MemberEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "member-events";

    public void sendBorrowedEvent(String bookId, String memberId)
    {
        BookBorrowedEvent borrowedEvent = new BookBorrowedEvent();
        borrowedEvent.setBookId(bookId);
        borrowedEvent.setMemberId(memberId);
        try {
            String eventJson = new ObjectMapper().writeValueAsString(borrowedEvent);

            kafkaTemplate.send(TOPIC, eventJson);
        } catch (Exception e) {
            System.err.println("Error publishing event: " + e.getMessage());
        }
    }
}
