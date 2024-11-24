package com.member.memberservice.Repository;

import com.member.memberservice.Model.BookBorrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowingRepository extends JpaRepository<BookBorrowing, Integer> {
}
