package net.hk.hk97.Repositories;

import net.hk.hk97.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findUserByNationid(long id);


}
