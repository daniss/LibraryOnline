package com.book.bookservice.Producer;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class BookEventProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public BookEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "book-events";

    public void sendBorrowedEvent(String bookId, String memberId)
    {
        String event = "Book Borrowed: Book ID " + bookId + " by Member " + memberId;
        kafkaTemplate.send(TOPIC, event);
    }
}
