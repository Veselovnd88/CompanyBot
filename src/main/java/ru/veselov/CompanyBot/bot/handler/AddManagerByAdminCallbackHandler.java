package ru.veselov.CompanyBot.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.veselov.CompanyBot.bot.UpdateHandler;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.dao.DivisionDAO;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.util.DivisionKeyboardUtils;
import ru.veselov.CompanyBot.util.MessageUtils;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AddManagerByAdminCallbackHandler implements UpdateHandler {
    @Value("${bot.adminId}")
    private Long adminId;
    private final DivisionDAO divisionDAO;
    private final DivisionKeyboardUtils divisionKeyboardUtils;
    private final AdminCache adminCache;
    @Autowired
    public AddManagerByAdminCallbackHandler(DivisionDAO divisionDAO, DivisionKeyboardUtils divisionKeyboardUtils, AdminCache adminCache) {
        this.divisionDAO = divisionDAO;
        this.divisionKeyboardUtils = divisionKeyboardUtils;
        this.adminCache = adminCache;
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        List<Division> all = divisionDAO.findAll();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        if(all.contains(data)) {
            Optional<Division> one = divisionDAO.findOne(data);
            if (one.isPresent()) {
                EditMessageReplyMarkup editMessageReplyMarkup = divisionKeyboardUtils.divisionChooseField(update, data);
                if(divisionKeyboardUtils.isMarked(editMessageReplyMarkup.getReplyMarkup(),data)){
                    adminCache.addDivision(userId, one.get());}
                //FIXME else remove from cache
                return null;

            }
        }
        if(data.equalsIgnoreCase("NONE")){
            return SendMessage.builder().chatId(userId)
                    .text("Менеджер отписан от упоминаний, вы можете удалить его из базы")
                    .build();
        }

        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR)
                .build();
    }
}
