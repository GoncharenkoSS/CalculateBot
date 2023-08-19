package Calculon88.bot.Repository;


import Calculon88.bot.Model.UserTG;
import Calculon88.bot.Model.Values;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValuesRepository extends JpaRepository<Values, Integer> {

    @Query(value = "SELECT * FROM values ORDER BY ID DESC LIMIT 5", nativeQuery = true)
    List<Values> findLast5values();
}
