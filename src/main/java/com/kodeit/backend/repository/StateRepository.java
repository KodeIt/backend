package com.kodeit.backend.repository;

import com.kodeit.backend.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    @Query(value = "select * from states as s where s.country_id = :countryId order by s.name asc", nativeQuery = true)
    List<State> findAllByCountryId(@Param("countryId") Long countryId);

}
