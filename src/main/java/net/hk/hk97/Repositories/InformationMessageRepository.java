package net.hk.hk97.Repositories;

import net.hk.hk97.Models.InformationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InformationMessageRepository extends JpaRepository<InformationMessage, Integer> {

    InformationMessage findInformationMessageByNames(String names);
}
