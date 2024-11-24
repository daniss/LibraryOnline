package com.member.memberservice.Repository;

import com.member.memberservice.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("SELECT m FROM Member m WHERE m.Email = :email")
    Member findByEmail(@Param("email") String email);
}
