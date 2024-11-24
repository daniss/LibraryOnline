package com.member.memberservice.Service;

import com.member.memberservice.Model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "book-service")
public interface BookServiceClient {
    @GetMapping("/api/v1/book")
    List<Book> findAll();

    @GetMapping("/api/v1/book/{id}")
    @CrossOrigin
    Book findById(@PathVariable("id") Integer id);

    @PutMapping("/api/v1/book/{id}")
    Book updateById(@PathVariable("id") Integer id, @RequestBody Book book);


}
