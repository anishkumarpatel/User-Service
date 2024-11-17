package com.unisys.udb.user.repository;


import com.unisys.udb.user.entity.DigitalCustomerPwd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DigitalCustomerPwdRepository extends JpaRepository<DigitalCustomerPwd, Long> {
    DigitalCustomerPwd findFirstByDigitalCustomerProfileIdOrderByPasswordChangeDateDesc(
            UUID digitalCustomerProfileId);

}
