package Calculon88.bot.Repository;


import Calculon88.bot.Model.UserTG;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserTG, Integer> {
    Optional findByIdTG(long idTG);
}
