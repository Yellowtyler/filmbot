package com.example.filmbot1.Repos;

import com.example.filmbot1.Model.Reminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Iterator;
import java.util.List;

@Service
public class ReminderServiceImp implements ReminderService {

    @Autowired
   private ReminderRepos reminderRepos;


    @Override
    public boolean check(Long chatid, String name, String date) {
        return reminderRepos.check(chatid, name, date) == null;
    }

    @Override
    public void save(Reminder rem) {

        reminderRepos.save(rem);

    }

    @Override
    public Iterable<Reminder> findAll() {
        return reminderRepos.findAll();
    }

    @Override
    public void delete(Reminder rem) {
        reminderRepos.delete(rem);
    }

    @Override
    public void deleteNotif(Long chatid, String name, String username) {
      reminderRepos.deleteNotif(chatid,name, username);
    }

    @Override
    public Iterable<Reminder> getFilms(Long chatid) {
        return reminderRepos.getFilms(chatid);
    }

    @Override
    public boolean existsByNameAndChatid(Long chatid, String name) {
        return reminderRepos.existsByNameAndChatid(chatid, name);
    }


}
