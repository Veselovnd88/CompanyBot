package ru.veselov.CompanyBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.CompanyBot.entity.Division;

import java.util.Optional;

public interface DivisionRepository extends JpaRepository<Division, Long> {


    @Query("SELECT d from Division d WHERE d.name = :name")
    public Optional<Division> findByName(@Param("name") String name);

    Optional<Division> findOneWithManagers(String id);

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
