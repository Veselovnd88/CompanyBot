package ru.veselov.companybot.cache;

import ru.veselov.companybot.bot.BotState;

public interface UserStateCache extends Clearable, Resetable {

    BotState getUserBotState(Long id);

    void setUserBotState(Long id, BotState botState);

}
