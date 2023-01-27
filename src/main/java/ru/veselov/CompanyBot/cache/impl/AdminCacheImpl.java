package ru.veselov.CompanyBot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.veselov.CompanyBot.cache.AdminCache;
import ru.veselov.CompanyBot.entity.Division;
import ru.veselov.CompanyBot.model.ManagerModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class AdminCacheImpl implements AdminCache {

    private final HashMap<Long, ManagerModel> managersCache = new HashMap<>();

    private final HashMap<Long, List<Division>> managersDivisions = new HashMap<>();

    @Override
    public void addManager(Long adminId,ManagerModel manager) {
        managersCache.put(adminId, manager);
    }

    @Override
    public ManagerModel getManager(Long adminId) {
        return managersCache.get(adminId);
    }

    @Override
    public void addDivision(Long userId, Division division) {
        if(managersDivisions.containsKey(userId)){
            managersDivisions.get(userId).add(division);

        }
        else{
            List<Division> list = List.of(division);
            managersDivisions.put(userId,list);
        }
    }

    @Override
    public List<Division> getDivision(Long userId) {
        return managersDivisions.get(userId);
    }

    @Override
    public void clear(Long userId) {
        managersCache.remove(userId);
    }
}
