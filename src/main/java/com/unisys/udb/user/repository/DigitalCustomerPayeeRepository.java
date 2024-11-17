
package com.unisys.udb.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalCustomerPayee;

@Repository
public interface DigitalCustomerPayeeRepository extends JpaRepository<DigitalCustomerPayee, Integer> {
     List<DigitalCustomerPayee> findByDigitalCustomerProfileId(UUID digitalCustomerProfileId);

     List<DigitalCustomerPayee> findByDigitalCustomerProfileIdOrderByPayeeNickName(UUID digitalCustomerProfileId);

     Optional<DigitalCustomerPayee> findByDigitalCustomerPayeeId(Integer digitalCustomerPayeeId);

     List<DigitalCustomerPayee> findByDigitalCustomerProfileIdOrderByPayeeCreationDateDesc(
               UUID digitalCustomerProfileId);

     @Query(value = QueryConstants.GET_PAYEE_TRANSACTIONS, nativeQuery = true)
     List<Object> getPayeeTrxDetails(@Param("digitalPayeeId") Integer digitalPayeeId);

     @Transactional
     void deleteByDigitalCustomerPayeeId(Integer payeeId);

}