package com.example.filmbot1.Repos;

import com.example.filmbot1.Model.Reminder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReminderRepos extends CrudRepository<Reminder, Long> {

    @Query(value = "select e.username from reminder e where e.chatid=:chatid and e.name=:name and e.date=:date", nativeQuery = true)
    @Transactional(readOnly=true)
    String check(@Param("chatid")Long chatid,@Param("name") String name, @Param("date") String date);

    @Modifying
    @Transactional
    @Query(value="delete from reminder e where e.chatid=:chatid and e.name=:name and e.username=:username",nativeQuery = true)
    void deleteNotif(@Param("chatid") Long chatid, @Param("name") String name,@Param("username") String username);

    @Query(value="select e.id, e.chatid, e.name, e.username, e.date from reminder e where e.chatid=:chatid",nativeQuery = true)
    @Transactional(readOnly=true)
    Iterable<Reminder> getFilms(@Param("chatid") Long chatid);

    @Query(value="SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM reminder u WHERE u.chatid = :chatid and u.name = :name", nativeQuery = true)
    @Transactional(readOnly=true)
    boolean existsByNameAndChatid(@Param("chatid") Long chatid, @Param("name") String name);
}
