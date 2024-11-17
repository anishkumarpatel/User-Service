package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalDocdbAlertRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitalDocdbAlertRefRepository extends JpaRepository<DigitalDocdbAlertRef, Integer> {
    DigitalDocdbAlertRef findByDigitalAlertKey(String digitalAlertKey);
}
