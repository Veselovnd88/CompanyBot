package ru.veselov.CompanyBot.cache;

import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.model.ManagerModel;

import java.util.List;

public interface AdminCache extends Cache{
    void addManager(Long adminId, ManagerModel manager);
    ManagerModel getManager(Long adminId);
    void addDivision(Long userId,Division division);
    List<Division> getDivision(Long userId);

}
