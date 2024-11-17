package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalActivityDetailRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitalActivityDetailRefRepository extends
        JpaRepository<DigitalActivityDetailRef, Integer> {
    DigitalActivityDetailRef findByDigitalActivityName(String digitalActivityName);
}
