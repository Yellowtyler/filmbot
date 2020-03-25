package com.example.filmbot1.Repos;

import com.example.filmbot1.Model.Reminder;



public interface ReminderService {


  boolean check(Long chatid,  String name, String date);

  void save(Reminder rem);

  Iterable<Reminder> findAll();

  void delete(Reminder rem);

  void deleteNotif(Long chatid, String name);

  Iterable<Reminder> getFilms(Long chatid);
}
