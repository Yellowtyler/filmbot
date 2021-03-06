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



@Component
public class Controller extends TelegramLongPollingBot{

    @Autowired
    private FilmApi filmApi;

    @Autowired
    private ReminderService reminderService;

    private String OldMessage="";
    private boolean isAdded;
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()&&update.getMessage().hasText())
        {

            int index = update.getMessage().getText().indexOf(" ");

            if(OldMessage.isEmpty()) {

                if (update.getMessage().getText().startsWith("/start")) {
                    String message = "Добро пожаловать!\n\nЧтобы добавить новое уведомление о выходе фильма\nвведите /add название фильма.\n\n" +
                            "Чтобы посмотреть ваши уведомления введите /show.\n\nЕсли вы хотите удалить уведомления,\nто введите /delete название фильма.\n\n" +
                            "Введите /help, если забыли команды.";
                    sendMessage(update.getMessage().getChatId(), message);

                }
                else if (update.getMessage().getText().startsWith("/upcomingmovies")) {

                    List<MovieDb> query = filmApi.getApi().getMovies().getUpcoming("ru", 1, "ru").getResults();
                    String textq = "";

                    for (MovieDb movieDb : query) {
                        if (findDiffDays(movieDb.getReleaseDate()) >= 0) {
                            textq += movieDb.getTitle() + "\n" + movieDb.getId() + "\n" + movieDb.getReleaseDate() + "\n" + movieDb.getOverview() + "\n" + movieDb.getStatus() + "\n" + "***********";
                        }


                    }


                    sendMessage(update.getMessage().getChatId(), textq);

                }
                else if (update.getMessage().getText().startsWith("/help")) {
                    String message = "/add название фильма - добавить новое уведомление\n" +
                            "/show - посмотреть свои уведомления\n" +
                            "/delete название фильма - удалить уведомление";

                    sendMessage(update.getMessage().getChatId(), message);
                }

                else if (update.getMessage().getText().startsWith("/add")) {

                    if(index > -1)  AddNotif(update);

                    else
                    {
                        OldMessage = update.getMessage().getText();
                        isAdded=true;
                        String message = "Введите название фильма, по которому вы хотите получать уведомления.";
                        sendMessage(update.getMessage().getChatId(), message);

                    }

                }

                else if (update.getMessage().getText().startsWith("/delete") ) {

                  if(index>-1)  DeleteNotif(update);

                  else
                  {
                      OldMessage = update.getMessage().getText();
                      isAdded=false;
                      String message = "Введите название фильма, по которому вы больше не хотите получать уведомления.";
                      sendMessage(update.getMessage().getChatId(), message);
                  }

                }



                else if (update.getMessage().getText().startsWith("/show")) {

                    ShowNotif(update);

                }

                else {

                    String message = "Команда набрана неверно! Для справки введите /help";
                    sendMessage(update.getMessage().getChatId(), message);


                }
            }


            else
            {
                if(isAdded)
                {
                    AddNotif(update);

                }

                else
                {
                    DeleteNotif(update);
                }

                OldMessage="";

            }


        }


    }
    private void sendMessage(long chatId, String message)
    {
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(message);

        try {
            setButtons(sendMessage);
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
        firstRow.add("/add");
        firstRow.add("/show");
        firstRow.add("/delete");
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

        int index =-1;
        if(update.getMessage().getText().contains("/add"))
        { index = update.getMessage().getText().indexOf(" ");}
        String text = update.getMessage().getText().substring(index+1);
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
            }
            else if (diffDays == 0) {
                message = "Фильм " + query.getTitle() + " вышел сегодня!";
            }
            else {
                Reminder rem = new Reminder();
               rem.setName(query.getTitle());
                rem.setDate(query.getReleaseDate());
                User user = update.getMessage().getFrom();
                rem.setUsername(user.getUserName());
                rem.setChatid(update.getMessage().getChatId());


              if (reminderService.check(update.getMessage().getChatId(), query.getTitle(), query.getReleaseDate())) {
                    reminderService.save(rem);
                    message = "Фильм "+ query.getTitle()+" добавлен! Не забудьте включить уведомления бота!";
                }
                else {
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



    @Scheduled(cron="0 0 18 * * ?")
    private void SendNotific()
    {
      updateDateRelease();
      String message = "";
      Map<String,Long> hashMap = new HashMap<String, Long>();
      Iterable<Reminder> reminders = reminderService.findAll();
      Iterator<Reminder> iter = reminders.iterator();
      long diffdate=0;
      int[] daysToRemind = new int[]{60,30,14,7};
      boolean flag =false;

     while(iter.hasNext()) {
          Reminder reminder = iter.next();
          if(reminder.getDate().contains("unknown"))
          {
              message="Дата выхода фильма "+ reminder.getName() + " неизвестна. Ждите обновления, возможно, фильм " +
                  "перенесли на другую дату.";

          }
          else {
              if (hashMap.containsKey(reminder.getDate())) {
                  diffdate = hashMap.get(reminder.getDate());
              }

              else {
                  diffdate = findDiffDays(reminder.getDate());
                  hashMap.put(reminder.getDate(), diffdate);
              }

              if (diffdate == 1) {

                  message = "Фильм " + reminder.getName() + " выходит завтра!";

              }

              else if (diffdate <= 0) {
                  message = "Фильм " + reminder.getName() + " сегодня вышел!";
                  flag = true;
              }

              else {
                  for (int i = 0; i < daysToRemind.length; i++) {

                      if (diffdate == daysToRemind[i]) {
                          message = "До выхода фильма " + reminder.getName() + " осталось " + diffdate + " дней. Дата релиза: " + reminder.getDate() + ".";
                          break;
                      }

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

    private void updateDateRelease() {
        Iterable<String> films = reminderService.getFilms();
        MovieDb query=null;
        String date="";
        for(String film: films)
        {
            query = filmApi.getApi().getSearch().searchMovie(film,null,"ru",true,1,"ru").getResults().get(0);
            if(query.getReleaseDate()!=null)
            {
                date=query.getReleaseDate();
            }

            else
            {
                date="unknown";

            }

            reminderService.updateNotif(date,film);
        }

    }


    private void DeleteNotif(Update update)
    {
        int index =-1;
        if(update.getMessage().getText().contains("/delete"))
        { index = update.getMessage().getText().indexOf(" ");}

        String text = update.getMessage().getText().substring(index+1);
        String message="";
        if(reminderService.existsByNameAndChatid(update.getMessage().getChatId(),text))
        {reminderService.deleteNotif(update.getMessage().getChatId(),text,update.getMessage().getFrom().getUserName());
            message ="Уведомление о выходе фильма "+ text+" успешно удалено.";
        }

      else message="Фильм "+ text +" не найден.";

      sendMessage(update.getMessage().getChatId(),message);
    }



    private void ShowNotif(Update update)
    {
        Iterable<Reminder> reminders = reminderService.getReminders(update.getMessage().getChatId());
        String message="Ваши уведомления: "+"\n";
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
        return "***********************";
    }

}
