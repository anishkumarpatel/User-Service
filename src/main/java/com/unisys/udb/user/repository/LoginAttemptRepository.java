package com.unisys.udb.user.repository;


import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface LoginAttemptRepository extends JpaRepository<DigitalCustomerProfile, Integer> {

    @Query(value = QueryConstants.GET_USER_LOGIN_ATTEMPT_DETAILS, nativeQuery = true)
    Integer updateLoginAttemptDetails(@Param("iCustUserName") String iCustomerName,
                                      @Param("iStatusName") String statusMessage,
                                      @Param("@iDeviceUid") String iDeviceUid, @Param("@iMaxCount") Integer iMaxCount);
}
