package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.dto.response.DigitalCustomerActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SesionHistoryRepository extends JpaRepository<DigitalCustomerActivity, Long> {
    @Query(value = QueryConstants.GET_CUSTOMER_SESSION_HISTORY_DETAILS, nativeQuery = true)
    List<Object[]> getCustomerSessionHistoryDetails(
            @Param("iCustomerId") UUID iCustomerId, @Param("ioffset_row_count") Integer ioffsetRowCount,
            @Param("ifetch_row_count") Integer ifetchRowCount, @Param("iactivityName") String iactivityName,
            @Param("iactivityChannel") String iactivityChannel, @Param("ifromDate")LocalDate ifrom,
            @Param("itoDate") LocalDate ito);
}