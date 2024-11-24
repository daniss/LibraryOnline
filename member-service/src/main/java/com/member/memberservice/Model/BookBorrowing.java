package com.member.memberservice.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class BookBorrowing {
    @Id
    @SequenceGenerator(
            name = "book_borrowing_id_sequence",
            sequenceName = "book_borrowing_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_borrowing_id_sequence"
    )
    private Integer id;
    private Integer bookId;
    @ManyToOne
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private Member member;
    private Date borrowDate;
    private Date returnDate;
    private boolean isReturned;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public BookBorrowing() {
        this.borrowDate = new Date();
        this.returnDate = calculateReturnDate(this.borrowDate);
    }

    private Date calculateReturnDate(Date borrowDate) {
        LocalDate borrowLocalDate = new java.sql.Date(borrowDate.getTime()).toLocalDate();
        LocalDate returnLocalDate = borrowLocalDate.plusDays(7);
        return java.sql.Date.valueOf(returnLocalDate);
    }

    public BookBorrowing(Integer id, Integer bookId, Integer userId, Date borrowDate, Date returnDate, boolean isReturned) {
        this.id = id;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.isReturned = isReturned;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }


    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }
}
