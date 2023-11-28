package ru.veselov.companybot.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.companybot.cache.AdminCache;
import ru.veselov.companybot.entity.DivisionEntity;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class AdminCacheImpl implements AdminCache {

    private final HashMap<Long, ManagerModel> managersCache = new HashMap<>();

    private final HashMap<Long, List<DivisionEntity>> managersDivisions = new HashMap<>();

    @Override
    public void addManager(Long adminId,ManagerModel manager) {
        managersCache.put(adminId, manager);
    }

    @Override
    public ManagerModel getManager(Long adminId) {
        return managersCache.get(adminId);
    }

    @Override
    public void addDivision(Long userId, DivisionEntity divisionEntity) {
        if(managersDivisions.containsKey(userId)){
            managersDivisions.get(userId).add(divisionEntity);

        }
        else{
            List<DivisionEntity> list = List.of(divisionEntity);
            managersDivisions.put(userId,list);
        }
    }

    @Override
    public List<DivisionEntity> getDivision(Long userId) {
        return managersDivisions.get(userId);
    }

    @Override
    public void clear(Long userId) {
        managersCache.remove(userId);
    }
}
