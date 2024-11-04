package com.kotan4ik.utbotmodule.storage.tariffs;

import com.kotan4ik.utbotmodule.dto.TariffsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TariffsRepository extends JpaRepository<Tariffs, Long> {

    @Query("select new com.kotan4ik.utbotmodule.dto.TariffsDto(t.t1, t.t2, t.t3, t.cold, t.hot) " +
            "from Tariffs t " +
            "where t.userId = ?1 " +
            "order by t.tariffId desc " +
            "limit 1")
    Optional<TariffsDto> getLastTariffs(Long userId);
}
