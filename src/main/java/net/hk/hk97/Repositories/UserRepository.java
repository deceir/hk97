package net.hk.hk97.Repositories;

import net.hk.hk97.Models.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration

public interface UserRepository extends JpaRepository<User, String> {

    User findUserByNationid(long id);

    User findUserByLeadernameLike(String name);


}
