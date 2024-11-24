package com.book.bookservice.Consumer;

import com.book.bookservice.Controller.BookController;
import com.book.bookservice.Event.BookBorrowedEvent;
import com.book.bookservice.Model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MemberEventConsumer {

    private final ObjectMapper objectMapper;
    private final BookController bookController;

    public MemberEventConsumer(ObjectMapper objectMapper, BookController bookController) {
        this.objectMapper = objectMapper;
        this.bookController = bookController;
    }

    @KafkaListener(topics = "member-events", groupId = "member-service-group")
    public void consumeBookEvent(String event) {
        try {
            BookBorrowedEvent borrowedEvent = objectMapper.readValue(event, BookBorrowedEvent.class);


            bookController.updateBookQuantity(borrowedEvent);

        } catch (Exception e) {
            System.err.println("Error processing the event: " + e.getMessage());
        }

    }
}
