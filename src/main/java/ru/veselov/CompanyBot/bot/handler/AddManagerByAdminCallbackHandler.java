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
        List<String> ids = all.stream().map(Division::getDivisionId).toList();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String data = update.getCallbackQuery().getData();
        if(ids.contains(data)) {
            Optional<Division> one = divisionDAO.findOne(data);
            if (one.isPresent()) {
                return divisionKeyboardUtils.divisionChooseField(update, data);
            }
        }
        if(data.equalsIgnoreCase("none")){
            return divisionKeyboardUtils.divisionChooseField(update,data);
        }
        if(data.equalsIgnoreCase("save")){
            //TODO save взять клавиатуру, пройти по каждой строке и узнать какие тру
        }

        return AnswerCallbackQuery.builder().callbackQueryId(update.getCallbackQuery().getId())
                .text(MessageUtils.ERROR)
                .build();
    }
}
