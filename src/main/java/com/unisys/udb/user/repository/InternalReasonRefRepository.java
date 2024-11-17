package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.InternalReasonRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalReasonRefRepository extends JpaRepository<InternalReasonRef, Integer> {
    @Query(value = QueryConstants.GET_REASON_BY_CATEGORY, nativeQuery = true)
    List<String> findReasonNameByReasonCategoryIgnoreCase(@Param("reasonCategory") String reasonCategory);
}
