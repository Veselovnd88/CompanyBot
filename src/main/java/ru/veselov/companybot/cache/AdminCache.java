package ru.veselov.companybot.cache;

import ru.veselov.companybot.entity.DivisionEntity;

import java.util.List;

public interface AdminCache extends Cache{
    void addManager(Long adminId, ManagerModel manager);
    ManagerModel getManager(Long adminId);
    void addDivision(Long userId, DivisionEntity divisionEntity);
    List<DivisionEntity> getDivision(Long userId);

}
