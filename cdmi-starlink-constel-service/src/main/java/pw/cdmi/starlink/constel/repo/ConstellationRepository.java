package pw.cdmi.starlink.constel.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.constel.modules.entities.Constellation;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, String>{

}
