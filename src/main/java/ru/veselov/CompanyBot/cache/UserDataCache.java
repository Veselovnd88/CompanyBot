package ru.veselov.CompanyBot.cache;

import ru.veselov.CompanyBot.bot.BotState;

public interface UserDataCache {
    BotState getUserBotState(Long id);
    void setUserBotState(Long id, BotState botState);



}
