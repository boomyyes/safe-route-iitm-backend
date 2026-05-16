package com.saferoute.repositories;

import com.saferoute.models.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    @Query(value = "SELECT * FROM complaints c WHERE c.status != 'RESOLVED' AND ST_DWithin(c.geom::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radiusMeters)", nativeQuery = true)
    List<Complaint> findActiveComplaintsNearLocation(@Param("longitude") double lon, @Param("latitude") double lat, @Param("radiusMeters") double radiusMeters);

}
