package Calculon88.bot.Repository;


import Calculon88.bot.Model.UserTG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserTG, Integer> {
    Optional findByIdTG(long idTG);

    @Query(value = "SELECT * FROM userTG ORDER BY ID DESC LIMIT 10", nativeQuery = true)
    List<UserTG> findLast10userTG();
}
