package net.hk.hk97.Repositories;

import net.hk.hk97.Models.Radiation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RadiationRepository extends JpaRepository<Radiation, String> {

    Radiation findRadiationByID(String ID);
}
