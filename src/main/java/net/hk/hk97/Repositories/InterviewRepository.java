package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Interview findInterviewByChannelId(long id);

}
