package com.kotan4ik.utbotmodule.storage.readings;

import com.kotan4ik.utbotmodule.dto.ReadingsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReadingsRepository extends JpaRepository<Readings, Integer> {

    @Query("select new com.kotan4ik.utbotmodule.dto.ReadingsDto(r.t1, r.t2, r.t3, r.cold, r.hot) " +
            "from Readings r " +
            "where r.userId = ?1 " +
            "order by r.readingId desc " +
            "limit 1")
    Optional<ReadingsDto> getLastReading(long userId);

    @Query("select new com.kotan4ik.utbotmodule.dto.ReadingsDto(r.t1, r.t2, r.t3, r.cold, r.hot) " +
            "from Readings r " +
            "where r.userId = ?1 " +
            "order by r.readingId desc " +
            "limit 1 offset 1")
    Optional<ReadingsDto> getPreviousReading(long userId);
}
