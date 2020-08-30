package pw.cdmi.starlink.station.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.cdmi.starlink.station.modules.entities.StationLocation;

@Repository
public interface StationLocationRepository extends JpaRepository<StationLocation, String>{

}
