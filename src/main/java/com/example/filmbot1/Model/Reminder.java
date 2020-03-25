package com.example.filmbot1.Model;

import javax.persistence.*;

@Entity
@Table(name="Reminder")
public class Reminder {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="date")
    private String date;

    @Column(name="username")
    private String username;

    @Column(name="chatid")
    private Long chatid;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatid() {
        return chatid;
    }

    public void setChatid(Long chatid) {
        this.chatid = chatid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public  Reminder()
    {

    }
}
