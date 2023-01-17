package ru.veselov.CompanyBot.cache;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.entity.Division;

import java.util.List;

public interface AdminCache extends Cache{
    void addManager(Long adminId, User user);
    User getManager(Long adminId);
    void addDivision(Long userId,Division division);
    List<Division> getDivision(Long userId);

}
