package com.example.filmbot1;

import com.example.filmbot1.Model.Reminder;
import com.example.filmbot1.Repos.ReminderService;
import com.example.lib.info.movito.themoviedbapi.TmdbMovies;
import com.example.lib.info.movito.themoviedbapi.model.MovieDb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
public class Controller extends TelegramLongPollingBot{

    @Autowired
    private FilmApi filmApi;

    @Autowired
    private ReminderService reminderService;


  @Override
    public void onUpdateReceived(Update update) {
        /*if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }*/
      //  chatId= update.getMessage().getChatId();



        if(update.hasMessage()&&update.getMessage().hasText())
        {
            SendMessage sendMessage=null;
           /* if(update.getMessage().getText().equals("/hi"))
            {
                User user = update.getMessage().getFrom();

             sendMessage=new SendMessage().setChatId(update.getMessage().getChatId()).setText("Hello "+user.getUserName());


            }
*/




           if(update.getMessage().getText().startsWith("/start")) {
               String message="Добро пожаловать! Чтобы добавить новое напоминание о выходе фильме, введите /sendnotif название фильма";
               sendMessage(update.getMessage().getChatId(), message);

           }
             int index = update.getMessage().getText().indexOf(" ");

           /*  if(update.getMessage().getText().startsWith("/movie")&&index>-1)
             {
               String text = update.getMessage().getText().substring(index);



                MovieDb query = filmApi.getApi().getSearch().searchMovie(text,null,"en",true,1,"ru").getResults().get(0);
                String textq = query.getTitle() +"\n" +query.getVoteAverage() + "\n"+query.getOverview() + "\n" +query.getImages();
                 sendMessage=new SendMessage().setChatId(update.getMessage().getChatId()).setText(textq);
             }
*/
            if(update.getMessage().getText().startsWith("/upcomingmovie"))
            {


                List<MovieDb> query = filmApi.getApi().getMovies().getUpcoming("ru",1,null).getResults();
                String textq="";

                for (MovieDb movieDb: query) {
                      if(findDiffDays(movieDb.getReleaseDate())>=0)
                      {
                         textq+=movieDb.getTitle()+"\n" +movieDb.getId()+"\n"+ movieDb.getReleaseDate()+"\n"+movieDb.getOverview()+ "\n"+movieDb.getStatus()+"\n"+"***********";
                      }


                }


                sendMessage(update.getMessage().getChatId(),textq);

            }
            //TODO сделать описание бота /start
            //TODO напоминание для сериалов
            if(update.getMessage().getText().startsWith("/addnotif")&&index>-1) {

               AddNotif(update);

            }

            if(update.getMessage().getText().startsWith("/deletenotif")&&index>-1) {

                 DeleteNotif(update);

            }



            if(update.getMessage().getText().startsWith("/shownotif")) {

              ShowNotif(update);

            }



        }


    }
    private void sendMessage(long chatId, String message)
    {
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(message);

        try {
            // setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void setButtons(SendMessage sendMessage)
    {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("/sendnotif");
        keyboardRows.add(firstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

    }


    private long findDiffDays(String dateRelease)
    {
        LocalDate nowtime = LocalDate.now();
        LocalDate daterealease = LocalDate.parse(dateRelease, DateTimeFormatter.ISO_LOCAL_DATE);
        Duration diff = Duration.between(nowtime.atStartOfDay(), daterealease.atStartOfDay());

        return diff.toDays();
    }





    private void AddNotif(Update update) {

        int index = update.getMessage().getText().indexOf(" ");
        String text = update.getMessage().getText().substring(index);
        MovieDb query=null;

        try
        {
            query = filmApi.getApi().getSearch().searchMovie(text,null,"ru",true,1,"ru").getResults().get(0);
        }

        catch (IndexOutOfBoundsException e)
        {
            sendMessage(update.getMessage().getChatId(),"Фильм "+ text +" не найден.");
            return;
        }

        String message="";

        if(query.getReleaseDate()!=null) {

            long diffDays = findDiffDays(query.getReleaseDate());


            if (diffDays < 0) {
                message = "Фильм " + query.getTitle() + " уже вышел! Дата релиза: " + query.getReleaseDate() + ".";
            } else if (diffDays == 0) {
                message = "Фильм " + query.getTitle() + " вышел сегодня!";
            } else {
                Reminder rem = new Reminder();
                rem.setName(query.getTitle());
                rem.setDate(query.getReleaseDate());
                User user = update.getMessage().getFrom();
                rem.setUsername(user.getUserName());
                rem.setChatid(update.getMessage().getChatId());
                    /*rem.setName("ff");
                    rem.setDate("2020-04-01");
                    User user = update.getMessage().getFrom();
                    rem.setUsername(user.getUserName());
                    rem.setChatid(update.getMessage().getChatId());*/

                //message = query.getReleaseDate();

                //reminderService.save(rem);

                if (reminderService.check(update.getMessage().getChatId(), query.getTitle(), query.getReleaseDate())) {
                    reminderService.save(rem);
                    message = "Фильм "+ query.getTitle()+" добавлен! Не забудьте включить уведомления бота!";
                } else {
                    message = "Уведомления о выходе этого фильма уже приходят.";
                }
            }
        }
        else
        {
            message="К сожалению, дата выхода фильма " + query.getTitle() +" еще неизвестна.";
        }
        sendMessage(update.getMessage().getChatId(),message);

    }




    //TODO сделать отмену отправки уведомлений
    //TODO исправить проблему с id в базе данных
    //TODO меню
    //TODO показать добавленные уведомления



   @Scheduled(cron="0 0 18 * * ?")
    private void SendNotific()
    {

      String message = "";
      Map<String,Long> hashMap = new HashMap<String, Long>();
      Iterable<Reminder> reminders = reminderService.findAll();
      Iterator<Reminder> iter = reminders.iterator();
      long diffdate=0;
      int[] daysToRemind = new int[]{60,30,14,7};
      boolean flag =false;

     while(iter.hasNext()) {
          Reminder reminder = iter.next();
          if (hashMap.containsKey(reminder.getDate()))
          {
              diffdate=hashMap.get(reminder.getDate());

          }

          else
          {
              diffdate=findDiffDays(reminder.getDate());
              hashMap.put(reminder.getDate(),diffdate);
          }

          if(diffdate==1)
          {

              message="Фильм " + reminder.getName() +" выходит завтра!";

          }

          else if(diffdate==0)
          {

              message="Фильм " + reminder.getName() +" сегодня вышел!";
              flag=true;
          }

          else
         {
             for(int i=0;i<daysToRemind.length;i++)
          {

              if(diffdate==daysToRemind[i])
              {
                  message="До выхода фильма " + reminder.getName() +" осталось " + diffdate+" дней. Дата релиза: " +reminder.getDate()+".";
                  break;
              }

          }
         }
          if(!message.isEmpty())
          {
              SendMessage sendMessage = new SendMessage().setChatId(reminder.getChatid()).setText(message);

              try {
                  execute(sendMessage);
              } catch (TelegramApiException e) {
                  e.printStackTrace();
              }
              if(flag)
              {reminderService.delete(reminder);
                  flag=false; }

          }

          message="";
      }

    }



    private void DeleteNotif(Update update)
    {
        int index = update.getMessage().getText().indexOf(" ");
        String text = update.getMessage().getText().substring(index);
        reminderService.deleteNotif(update.getMessage().getChatId(),text);
        String message ="Уведомление успешно удалено.";
        sendMessage(update.getMessage().getChatId(),message);
    }



    private void ShowNotif(Update update)
    {
        Iterable<Reminder> reminders = reminderService.getFilms(update.getMessage().getChatId());
        String message="";
        for(Reminder reminder:reminders)
        {
            message +=reminder.getName()+" - " +reminder.getDate()+"\n";


        }

        sendMessage(update.getMessage().getChatId(),message);

    }


    @Override
    public String getBotUsername() {
        return "filmnot_bot";
    }

    @Override
    public String getBotToken() {
        return "1*******:**************";
    }
}
