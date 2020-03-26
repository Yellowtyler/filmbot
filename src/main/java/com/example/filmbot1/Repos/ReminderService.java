package com.example.filmbot1.Repos;

import com.example.filmbot1.Model.Reminder;

import java.util.List;


public interface ReminderService {


  boolean check(Long chatid,  String name, String date);

  void save(Reminder rem);

  Iterable<Reminder> findAll();

  void delete(Reminder rem);

  void deleteNotif(Long chatid, String name, String username);

  Iterable<Reminder> getFilms(Long chatid);

  boolean existsByNameAndChatid( Long chatid, String name);


}
