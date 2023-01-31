package ru.veselov.CompanyBot.bot.handler;

import org.aspectj.weaver.ast.Call;
import org.telegram.telegrambots.meta.api.objects.*;

import static org.mockito.Mockito.spy;

public class UserActions {

    private Update update=spy(Update.class);
    private Message message=spy(Message.class);
    private User user=spy(User.class);
    private User userFrom=spy(User.class);
    private CallbackQuery callbackQuery=spy(CallbackQuery.class);
    private MessageEntity messageEntity=spy(MessageEntity.class);


    public void userPressStart(User user){
        setUpUpdateMessage();

        message.setText("/start");
        message.setFrom(user);
        user.setId(user.getId());
    }



    private void setUpUpdateMessage(){
        update.setCallbackQuery(null);
        update.setMyChatMember(null);
        update.setMessage(message);
    }


}
