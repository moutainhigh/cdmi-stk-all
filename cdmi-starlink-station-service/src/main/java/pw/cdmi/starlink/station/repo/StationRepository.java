package pw.cdmi.starlink.station.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.station.modules.entities.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, String>{

}
