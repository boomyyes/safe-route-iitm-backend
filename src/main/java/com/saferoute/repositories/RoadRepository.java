package com.saferoute.repositories;

import com.saferoute.models.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoadRepository extends JpaRepository<Road, UUID> {

    @Query(value = "SELECT * FROM roads r WHERE ST_DWithin(r.geom::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radiusMeters)", nativeQuery = true)
    List<Road> findRoadsNearLocation(@Param("longitude") double lon, @Param("latitude") double lat, @Param("radiusMeters") double radiusMeters);

}
