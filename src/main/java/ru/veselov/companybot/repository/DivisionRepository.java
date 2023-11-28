package ru.veselov.companybot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.companybot.entity.DivisionEntity;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<DivisionEntity, Long> {


    @Query("SELECT d from DivisionEntity d WHERE d.name = :name")
    public Optional<DivisionEntity> findByName(@Param("name") String name);

    Optional<DivisionEntity> findOneWithManagers(String id);

    /*@Transactional
    //FIXME move to service
    public void deleteById(String id) {
        Optional<Division> division = findOneWithManagers(id);
        if (division.isPresent()) {
            for (ManagerEntity me : division.get().getManagers()) {
                me.removeDivision(division.get());
            }
            delete(division.get());
        }
    }*/

}
