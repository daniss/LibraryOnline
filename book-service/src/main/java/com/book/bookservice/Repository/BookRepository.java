package com.book.bookservice.Repository;

import com.book.bookservice.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByIdAndQuantityGreaterThan(Integer id, Integer quantity);
}
