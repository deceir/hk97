package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {

    Recruit findById(long id);

}