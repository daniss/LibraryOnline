package com.member.memberservice.Model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String FirstName;
    private String LastName;
    private String Email;
    private String Password;
    private String Username;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    @Override
    public String toString() {
        return "Member{" +
                "Id=" + Id +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Username='" + Username + '\'' +
                ", bookBorrowingList=" + bookBorrowingList +
                '}';
    }

    public void setUsername(String username) {
        Username = username;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private List<BookBorrowing> bookBorrowingList = new ArrayList<>();

    public List<BookBorrowing> getBookBorrowingList() {
        return bookBorrowingList;
    }

    public void setBookBorrowingList(List<BookBorrowing> bookBorrowingList) {
        this.bookBorrowingList = bookBorrowingList;
    }

    public Member() {

    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Member(Integer id,
                  String firstName,
                  String lastName,
                  String email,
                  String password,
                  List<BookBorrowing> bookBorrowingList) {
        Id = id;
        FirstName = firstName;
        bookBorrowingList = bookBorrowingList;
        LastName = lastName;
        Email = email;
        Password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(Id, member.Id) && Objects.equals(FirstName, member.FirstName) && Objects.equals(LastName,
                member.LastName) && Objects.equals(Email, member.Email) && Objects.equals(Password, member.Password) &&
                Objects.equals(bookBorrowingList, member.bookBorrowingList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, FirstName, LastName, Email, Password);
    }
}
