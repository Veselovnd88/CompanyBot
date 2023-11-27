package ru.veselov.CompanyBot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.CompanyBot.entity.ManagerEntity;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerEntity, Long> {

    //private final DivisionRepository divisionRepository;

    /* @Transactional //TODO move to service
    public void save(ManagerEntity managerEntity){
        Optional<ManagerEntity> optionalManager = findOne(managerEntity.getManagerId());
        if(optionalManager.isPresent()){
            managerEntity=optionalManager.get();
                for(Division d: managerEntity.getDivisions()){
                    Optional<Division> one = divisionRepository.findOne(d.getDivisionId());
                    if(one.isPresent()){
                        managerEntity.addDivision(one.get());
                    }
                    else{
                        managerEntity.addDivision(d);
                    }
            }
        }
        entityManager.persist(managerEntity);
    }*/

    /* @Transactional //TODO move to service
    public void saveWithDivisions(ManagerEntity managerEntity, Set<Division> divisionSet){
        Optional<ManagerEntity> optManager = findOne(managerEntity.getManagerId());
        if(optManager.isEmpty()) {
            for (Division d : divisionSet) {
                Optional<Division> one = divisionRepository.findOne(d.getDivisionId());
                if (one.isPresent()) {
                    managerEntity.addDivision(one.get());
                } else {
                    managerEntity.addDivision(d);
                }
            }
        }
        else{
            managerEntity=optManager.get();
            List<Division> allDivs = divisionRepository.findAll();
            for(Division d: allDivs){
                if(divisionSet.contains(d)){
                    managerEntity.addDivision(d);
                }
                else{
                    managerEntity.removeDivision(d);
                }
            }
        }
        entityManager.persist(managerEntity);
    }*/


    /*@Transactional//TODO move to service
    public ManagerEntity update(ManagerEntity manager){
        for(Division d: manager.getDivisions()){
            Optional<Division> one = divisionRepository.findOne(d.getDivisionId());
            if(one.isPresent()){
                manager.addDivision(one.get());
            }
            else{
                manager.addDivision(d);
            }
        }
        entityManager.persist(manager);
        return manager;
    }
*/

    Optional<ManagerEntity> findOneWithDivisions(Long managerId);

    /*@Transactional //move to service
    public void removeDivisions(ManagerEntity manager){
        Optional<ManagerEntity> optManager = findOne(manager.getManagerId());
        if(optManager.isPresent()) {
            ManagerEntity managerEntity = optManager.get();
            Set<Division> divisions = Set.copyOf(managerEntity.getDivisions());
            for (Division d : divisions) {
                Optional<Division> one = divisionRepository.findOne(d.getDivisionId());
                if (one.isPresent()) {
                    managerEntity.removeDivision(d);
                }
            }entityManager.persist(managerEntity);
        }
    }
*/
}
