package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.DigitalLanguageRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalLanguageRefRepository extends JpaRepository<DigitalLanguageRef, Integer> {


    @Query(value = QueryConstants.FETCH_ALL_LOCALE_CODE_BY_LANGUAGE_ENABLED_FLAG, nativeQuery = true)
    List<String> fetchAllLocaleCodeByLanguageEnabledFlag();
}
