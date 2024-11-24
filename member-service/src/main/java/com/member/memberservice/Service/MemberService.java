package com.member.memberservice.Service;

import com.member.memberservice.Model.BookBorrowing;
import com.member.memberservice.Model.Member;
import com.member.memberservice.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;


    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Member findById(int id) {
        return memberRepository.findById(id).orElse(null);
    }

    public boolean isAlreadyBorrowedBook(int memberId, int bookId) {
        return memberRepository.findById(memberId)
                .map(member -> member.getBookBorrowingList()
                        .stream()
                        .anyMatch(borrowedBook -> borrowedBook.getBookId().equals(bookId)))
                .orElse(false);
    }

    public boolean existsById(int id) {
        return memberRepository.existsById(id);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
