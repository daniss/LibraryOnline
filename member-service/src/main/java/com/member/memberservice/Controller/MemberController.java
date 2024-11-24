package com.member.memberservice.Controller;

import com.member.memberservice.Model.BookBorrowing;
import com.member.memberservice.Producer.MemberEventProducer;
import com.member.memberservice.Repository.BookBorrowingRepository;
import com.member.memberservice.Service.BookServiceClient;
import com.member.memberservice.Model.Book;
import com.member.memberservice.Model.Member;
import com.member.memberservice.Service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/member")
public class MemberController {

    private final BookServiceClient bookServiceClient;
    private final BookBorrowingRepository bookBorrowingRepository;
    private final MemberEventProducer memberEventProducer;
    private final MemberService memberService;

    public MemberController(BookServiceClient bookServiceClient,
                            MemberEventProducer memberEventProducer,
                            MemberService memberService,
                            BookBorrowingRepository bookBorrowingRepository) {
        this.memberService = memberService;
        this.bookServiceClient = bookServiceClient;
        this.memberEventProducer = memberEventProducer;
        this.bookBorrowingRepository = bookBorrowingRepository;
    }

    @GetMapping
    @CrossOrigin
    public List<Member> getMembers() {
        return memberService.getMembers();
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        Member newMember = new Member();
        newMember.setFirstName(member.getFirstName());
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(newMember));
    }

    public void createMemberVoid(Member member) {
        Member newMember = new Member();
        newMember.setFirstName(member.getFirstName());
        newMember.setLastName(member.getLastName());
        newMember.setEmail(member.getEmail());
        newMember.setPassword(member.getPassword());
        newMember.setUsername(member.getUsername());
        memberService.createMember(newMember);
    }

    @GetMapping("/member/{memberId}")
    @CrossOrigin
    public ResponseEntity<List<Book>> getMemberBook(@PathVariable Integer memberId) {
        Member member = memberService.findById(memberId);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Integer> bookIds = member.getBookBorrowingList()
                .stream()
                .map(BookBorrowing::getBookId)
                .toList();
        List<Book> books = new ArrayList<>();
        Integer i = 0;
        for (Integer bookId : bookIds) {
            Book book = new Book();
            book = bookServiceClient.findById(bookId);
            books.add(book);
        }
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @GetMapping("/{memberId}/borrow/{bookId}")
    @CrossOrigin
    public boolean isAlreadyBorrowedBook(
            @PathVariable("bookId") Integer bookId,
            @PathVariable("memberId") Integer memberId) {
        return memberService.isAlreadyBorrowedBook(memberId, bookId);
    }

    @PostMapping("/{memberId}/borrow/{bookId}")
    @CrossOrigin
    public ResponseEntity<String> borrowBook(@PathVariable("memberId") Integer memberId,
                                             @PathVariable("bookId") Integer bookId) {
        Book book = bookServiceClient.findById(bookId);

        if (!memberService.existsById(memberId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member Not Found");
        }

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }

        if (book.getQuantity() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book is out of stock.");
        }

        Member member = memberService.findById(memberId);

        memberEventProducer.sendBorrowedEvent(String.valueOf(bookId), String.valueOf(memberId));
        BookBorrowing bookBorrowing = new BookBorrowing();
        bookBorrowing.setBookId(bookId);
        bookBorrowing.setMember(member);
        bookBorrowingRepository.save(bookBorrowing);

        // Return a success message
        return ResponseEntity.ok("Book borrowed successfully!");
    }

    public Member findMemberByEmail(String email) {
        return memberService.findMemberByEmail(email);
    }
}
