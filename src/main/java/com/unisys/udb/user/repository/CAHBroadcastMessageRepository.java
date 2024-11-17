package com.unisys.udb.user.repository;

import com.unisys.udb.user.constants.QueryConstants;
import com.unisys.udb.user.entity.cah.BroadcastMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CAHBroadcastMessageRepository extends JpaRepository<BroadcastMessage, Integer> {
    @Query(value = QueryConstants.CAH_GET_BROADCAST_MESSAGES_BY_STATUS, nativeQuery = true)
    List<Object[]> getListOfBroadcastMessages(String status);

    @Query(value = QueryConstants.FETCH_COMPLETED_BROADCAST_MESSAGES, nativeQuery = true)
    List<Object[]> fetchAllCompletedBroadcastMsgs(LocalDateTime currentTime);

    @Query(value = QueryConstants.GET_COUNT_OF_LIVE_BROADCAST_MESSAGES, nativeQuery = true)
    Integer getCountOfLiveBroadcastMessages(LocalDateTime currentTime, List<Integer> messageIdsList);

    @Modifying
    @Transactional
    @Query(value = QueryConstants.UPDATE_LIVE_MESSAGES_STATUS_TO_WITHDRAW, nativeQuery = true)
    void updateLiveMessagesStatusToWithdraw(String templateStatusName, List<Integer> messageIdsList,
                                            Timestamp modificationDate, String modifiedBy);


    @Query(value = QueryConstants.LIVE_BROADCAST_MESSAGES_QUERY, nativeQuery = true)
    List<Object[]> getLiveBroadcastMessages(LocalDateTime currentTime);

    @Query(value = QueryConstants.UPCOMING_BROADCAST_MESSAGES_QUERY, nativeQuery = true)
    List<Object[]> getUpcomingBroadcastMessages(LocalDateTime currentTime);

    @Query(value = QueryConstants.CAH_GET_BROADCAST_MESSAGE_BY_ID, nativeQuery = true)
    List<Object[]> getBroadcastMessagesById(Integer id);

    Optional<BroadcastMessage> findByMessageId(Integer messageId);

    @Query(value = QueryConstants.FIND_ID_BY_TEMPLATE_STATUS_NAME, nativeQuery = true)
    Optional<Integer> findIdByTemplateStatusName(@Param("templateStatusName") String templateStatusName);

    @Query(value = QueryConstants.CAH_GET_COUNT_BROADCAST_MESSAGES_FOR_ID_LIST, nativeQuery = true)
    Integer getCountOfBroadcastMessagesForIdList(List<Integer> ids);

    @Modifying
    @Transactional
    @Query(value = QueryConstants.DELETE_BROADCAST_MESSAGE_BY_ID_ACCOUNT_TYPE, nativeQuery = true)
    void deleteBroadCastMessage(String templateStatusName, List<Integer> ids,
                                Timestamp modificationDate, String modifiedBy);

    @Query(value = QueryConstants.CAH_GET_BROADCAST_MESSAGE_BY_ID_OR_NAME, nativeQuery = true)
    List<Object[]> getBroadcastMessagesByIdOrName(@Param("nameOrId") String nameOrId);
}