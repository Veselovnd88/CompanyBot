package ru.veselov.CompanyBot.cache;

import ru.veselov.CompanyBot.entity.ManagerEntity;

public interface AdminCache extends Cache{
    void addManager(ManagerEntity manager);

}
