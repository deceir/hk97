package net.hk.hk97.Repositories;

import net.hk.hk97.Models.AllianceKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllianceKeyRepository extends JpaRepository<AllianceKeys, Long> {

    AllianceKeys findAllianceKeysByAaName(String name);


}
