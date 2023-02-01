package ru.veselov.CompanyBot.bot.handler;

import org.telegram.telegrambots.meta.api.objects.*;

import java.util.List;

import static org.mockito.Mockito.spy;

public class UserActions {
    private User userFrom=spy(User.class);


    public Update userPressStart(User user){
        Update update = setUpUpdateMessage(user, "/start");
        MessageEntity messageEntity = setUpMessageEntity("/start");
        update.getMessage().setEntities(List.of(messageEntity));
        return update;
    }

    public Update userPressInquiry(User user){
        Update update = setUpUpdateMessage(user, "/inquiry");
        MessageEntity messageEntity = setUpMessageEntity("/inquiry");
        update.getMessage().setEntities(List.of(messageEntity));
        return update;
    }

    public Update userPressInquiryButton(User user){
        return setUpCallbackUpdate(user, "LEUZE");
    }
    public Update userPressInquiryButtonAnother(User user){
        return setUpCallbackUpdate(user, "COMMON");
    }

    public Update userSendMessage(User user){
        return setUpUpdateMessage(user, "Test Text");
    }
    public Update userPressContactButton(User user){
        return setUpCallbackUpdate(user, "contact" );
    }

    public Update userChooseContactButton(User user, String name){
        return setUpCallbackUpdate(user, name);
    }
    public Update userInputContactData(User user, String data){
        return setUpUpdateMessage(user, data);
    }





    private Update setUpUpdateMessage(User user, String text){
        Update update = new Update();
        Message message = new Message();
        update.setCallbackQuery(null);
        update.setMyChatMember(null);
        update.setMessage(message);
        message.setForwardFrom(null);
        message.setFrom(user);
        message.setText(text);
        return update;
    }

    private MessageEntity setUpMessageEntity(String command){
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType("bot_command");
        messageEntity.setLength(command.length());
        messageEntity.setOffset(0);
        return messageEntity;
    }

    private Update setUpCallbackUpdate(User user, String data){
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setFrom(user);
        callbackQuery.setData(data);
        callbackQuery.setId(user.getId() +"1");
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(user.getId());
        message.setChat(chat);
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);

        return update;
    }


}
