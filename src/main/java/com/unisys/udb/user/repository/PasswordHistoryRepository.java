package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

    /**
     * Retrieves old passwords for a given digital customer profile ID and date range.
     *
     * @param digitalCustomerProfileId The UUID of the digital customer profile
     * @param fromDate The start date of the date range
     * @param toDate The end date of the date range
     * @return A list of old passwords
     */
    @Query(value = QueryConstants.FETCH_OLD_PASSWORDS_QUERY, nativeQuery = true)
    List<String> findOldPasswordsByDigitalCustomerProfileIdAndDateRange(
            @Param("digitalCustomerProfileId") UUID digitalCustomerProfileId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
