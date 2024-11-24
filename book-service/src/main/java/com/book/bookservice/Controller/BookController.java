package com.book.bookservice.Controller;

import com.book.bookservice.Event.BookBorrowedEvent;
import com.book.bookservice.Model.Book;
import com.book.bookservice.Producer.BookEventProducer;
import com.book.bookservice.Repository.BookRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/book")
public class BookController {

    private final BookRepository bookRepository;

    private final BookEventProducer bookEventProducer;

    public BookController(BookRepository bookRepository, BookEventProducer bookEventProducer) {
        this.bookRepository = bookRepository;
        this.bookEventProducer = bookEventProducer;
    }

    @GetMapping
    @CrossOrigin
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    @CrossOrigin
    public Book getBookById(@PathVariable("id") Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    record NewBook(
            String title,
            String author,
            String publisher,
            String description,
            Integer quantity
    ){}

    public void updateBookQuantity(BookBorrowedEvent bookBorrowedEvent) {
        Book toUpdate = bookRepository.findById(Integer.valueOf(bookBorrowedEvent.getBookId())).orElse(null);
        if (toUpdate == null) {
            return;
        }
        toUpdate.setQuantity(toUpdate.getQuantity() - 1);
        bookRepository.save(toUpdate);
    }

    @PostMapping
    @CrossOrigin
    public void addBook(@RequestParam("title") String title,
                        @RequestParam("author") String author,
                        @RequestParam("publisher") String publisher,
                        @RequestParam("quantity") int quantity,
                        @RequestParam("description") String description,
                        @RequestParam("image") MultipartFile imageFile) throws IOException {

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setDescription(description);
        book.setQuantity(quantity);
        book.setImage(imageFile.getBytes());
        bookRepository.save(book);
    }

    @GetMapping("/{id}/image")
    @CrossOrigin
    public byte[] getImage(@PathVariable("id") Integer id) {
        return bookRepository.findById(id).get().getImage();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Book updateBook(@PathVariable("id") Integer id, @RequestBody NewBook book) {
        Book toUpdate = bookRepository.findById(id).orElse(null);
        if (toUpdate == null) {
            return null;
        }
        if (book.author != null) {
            toUpdate.setAuthor(book.author());
        }
        if (book.publisher != null) {
            toUpdate.setPublisher(book.publisher());
        }
        if (book.description != null) {
            toUpdate.setDescription(book.description());
        }
        if (book.quantity != null) {
            toUpdate.setQuantity(book.quantity());
        }
        if (book.title != null) {
            toUpdate.setTitle(book.title());
        }
        return bookRepository.save(toUpdate);
    }
}
