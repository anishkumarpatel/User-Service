
package com.unisys.udb.user.service.impl;

import static com.unisys.udb.user.constants.UdbConstants.DATE_FORMAT;
import static com.unisys.udb.user.constants.UdbConstants.LANGUAGE_CODE;
import static com.unisys.udb.user.constants.UdbConstants.LANGUAGE_PREFERENCE;
import static com.unisys.udb.user.constants.UdbConstants.USER_SERVICE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPayeeResponse;
import com.unisys.udb.user.dto.response.PayeeResponse;
import com.unisys.udb.user.entity.DigitalCustomerPayee;
import com.unisys.udb.user.entity.FundTransferDropDown;
import com.unisys.udb.user.exception.FundTransferValuesNotFound;
import com.unisys.udb.user.repository.DigitalCustomerPayeeRepository;
import com.unisys.udb.user.service.DigitalPayeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalPayeeServiceImpl implements DigitalPayeeService {

     private final DigitalCustomerPayeeRepository digitalCustomerPayeeRepository;
     private final UserInfoServiceImpl userInfoService;
     private UUID digitalCustomerProfileId;
     private String sortBy;
     private final MongoTemplate mongoTemplate;

     private final NotificationUtil notificationUtil;

     @Override
     public void createPayee(PayeeRequest payeeRequest) {
          DigitalCustomerPayee digitalCustomerPayee = DigitalCustomerPayee.builder()
                    .digitalCustomerProfileId(payeeRequest.getDigitalCustomerProfileId())
                    .payeeName(payeeRequest.getPayeeName()).payeeBankName(payeeRequest.getPayeeBankName())
                    .payeeAccountNumber(payeeRequest.getPayeeAccountNumber())
                    .payeeNickName(payeeRequest.getPayeeNickname()).payeeBankCode(payeeRequest.getPayeeBankCode())
                    .payeeReference(payeeRequest.getPayeeReference()).build();
          digitalCustomerPayee.setPayeeCreationDate(LocalDateTime.now());
          digitalCustomerPayeeRepository.save(digitalCustomerPayee);
          // adding logic for sending notification when payee is successfully added//
          try {

               notificationUtil.sendNotification(notificationUtil.prepareRequiredFieldsMap(
                         payeeRequest.getDigitalCustomerProfileId(), null, USER_SERVICE, "triggerAddPayeeNotification",
                         "AddPayee Template", LANGUAGE_PREFERENCE), new HashMap<>());
          } catch (Exception e) {
               log.error("payee added successfully but failed to send notification for customerprofileID"
                         + payeeRequest.getDigitalCustomerProfileId());
               log.error("error ####" + e.getMessage());
          }
     }

     public List<DigitalCustomerPayeeResponse> getAllPayees(UUID digitalCustomerProfileId, String sortBy) {

          log.info("Entering getAllPayees for digitalCustomerProfileId: {}", digitalCustomerProfileId);
          this.digitalCustomerProfileId = digitalCustomerProfileId;
          this.sortBy = sortBy;

          List<DigitalCustomerPayee> payeeDetails;

          if (!StringUtils.isBlank(sortBy) && sortBy.equalsIgnoreCase(UdbConstants.NAME)) {
               payeeDetails = digitalCustomerPayeeRepository
                         .findByDigitalCustomerProfileIdOrderByPayeeNickName(digitalCustomerProfileId);
               log.info("Retrieved {} Payee Details from the repository. with sorting on name", payeeDetails.size());
               return (payeeDetails.stream().map(this::mapToDigitalPayeeResponse)).toList();

          } else {

               payeeDetails = digitalCustomerPayeeRepository
                         .findByDigitalCustomerProfileIdOrderByPayeeCreationDateDesc(digitalCustomerProfileId);
               log.info("Retrieved {} Payee Details from the repository. with sorting on creation date",
                         payeeDetails.size());
               return (payeeDetails.stream().map(this::mapToDigitalPayeeResponse)).toList();

          }

     }

     /**
      * Maps a {@link DigitalCustomerPayee} object to a
      * {@link DigitalCustomerPayeeResponse} object.
      *
      * @return A new DigitalCustomerPayeeResponse object.
      */
     private DigitalCustomerPayeeResponse mapToDigitalPayeeResponse(DigitalCustomerPayee digitalCustomerPayee) {
          log.info("Mapping DigitalCustomerPayee to DigitalCustomerPayeeResponse: {}", digitalCustomerPayee);

          String payeeCreationDate = (digitalCustomerPayee.getPayeeCreationDate() != null)
                    ? digitalCustomerPayee.getPayeeCreationDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                    : null;

          return new DigitalCustomerPayeeResponse(digitalCustomerPayee.getDigitalCustomerPayeeId(),
                    digitalCustomerPayee.getDigitalCustomerProfileId(), digitalCustomerPayee.getPayeeName(),
                    digitalCustomerPayee.getPayeeBankName(), digitalCustomerPayee.getPayeeAccountNumber(),
                    digitalCustomerPayee.getPayeeNickName(), digitalCustomerPayee.getPayeeBankCode(),
                    digitalCustomerPayee.getPayeeReference(), payeeCreationDate);

     }

     @Override
     public List<FundTransferDropDown> getFundTransferDropdownValues(String languageCode) {
          log.debug("Entering getFundTransferDropdownValues for languageCode: {}", languageCode);
          Query query = new Query(Criteria.where(LANGUAGE_CODE).is(languageCode));
          List<FundTransferDropDown> fundTransferDropDowns = mongoTemplate.find(query, FundTransferDropDown.class);
          if (fundTransferDropDowns.isEmpty()) {
               throw new FundTransferValuesNotFound("Fund Transfer Dropdown values not fount ");
          }
          return fundTransferDropDowns;
     }

     @Transactional
     public boolean updatePayeeDetails(Integer digitalCustomerPayeeId, String payeeNickName, String payeeReference,
               String userName) throws DataAccessException {

          log.info("Updating payee details  with ID {} ", digitalCustomerPayeeId);
          if (payeeNickName == null && payeeReference == null) {
               return false;
          } else {

               return Optional
                         .ofNullable(
                                   digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(digitalCustomerPayeeId))
                         .map(existingPayee -> updatePayeeData(existingPayee.get(), payeeNickName, payeeReference,
                                   userName))
                         .orElse(false);
          }

     }

     private boolean updatePayeeData(DigitalCustomerPayee payeeDetails, String payeeNickName, String payeeReference,
               String userName) {

          if (payeeNickName != null) {
               payeeDetails.setPayeeNickName(payeeNickName.length() > 0 ? payeeNickName : null);

          }

          if (payeeReference != null) {
               payeeDetails.setPayeeReference(payeeReference.length() > 0 ? payeeReference : null);
          }

          payeeDetails.setPayeeModificationDate(
                    LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                              DateTimeFormatter.ofPattern(DATE_FORMAT)));
          payeeDetails.setPayeeModifiedBy(userName);

          digitalCustomerPayeeRepository.save(payeeDetails); // Saving the updated payee details
          log.info("Changes Updated Successfully for id" + payeeDetails.getDigitalCustomerPayeeId());
          try {

               notificationUtil.sendNotification(
                         notificationUtil.prepareRequiredFieldsMap(payeeDetails.getDigitalCustomerProfileId(), null,
                                   USER_SERVICE, "triggerUpdatePayeeNotification", "UpdatePayee Template",
                                   LANGUAGE_PREFERENCE),
                         new HashMap<>());

          } catch (Exception e) {
               log.error("payee updated successfully but failed to send notification"
                         + payeeDetails.getDigitalCustomerPayeeId());
          }
          return true;

     }

     public DigitalCustomerPayeeResponse getPayeeData(Integer digitalCustomerPayeeId) {
          log.info("Entering getPayeeData for digitalCustomerPayeeId: Single Payee Details{}", digitalCustomerPayeeId);

          return Optional
                    .ofNullable(digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(digitalCustomerPayeeId))
                    .map(existingPayee -> {
                         log.info("Retrieved {} Payee Details from the repository For GetPayeeData.",
                                   existingPayee.get().toString());
                         return mapToDigitalPayeeResponse(existingPayee.get());
                    }).orElse(null);

     }

     @Override
     public List<DigitalCustomerPayeeResponse> deletePayeeServe(List<Integer> digitalCustomerPayeeIds) {
          List<DigitalCustomerPayeeResponse> digitalCustomerPayeeList = getAllPayees(digitalCustomerProfileId, sortBy);

          List<Integer> existingPayeeIds = digitalCustomerPayeeList.stream()
                    .map(DigitalCustomerPayeeResponse::getDigitalCustomerPayeeId).toList();

          for (Integer id : digitalCustomerPayeeIds) {
               if (!existingPayeeIds.contains(id)) {
                    return getAllPayees(digitalCustomerProfileId, sortBy);
               }
          }

          digitalCustomerPayeeRepository.deleteAllById(digitalCustomerPayeeIds);
          try {
               if (!StringUtils.isBlank(String.valueOf(digitalCustomerProfileId))) {
                    notificationUtil.sendNotification(
                              notificationUtil.prepareRequiredFieldsMap(digitalCustomerProfileId, null, USER_SERVICE,
                                        "triggerDeletePayeeNotification", "DeletePayee Template", LANGUAGE_PREFERENCE),
                              new HashMap<>());
               }
          } catch (Exception e) {
               log.error("payee deleted successfully but failed to send notification");
          }
          return getAllPayees(digitalCustomerProfileId, sortBy);
     }

     public PayeeResponse getPayee(Integer digitalCustomerPayeeId) {
          log.info("Entering getPayee for digitalCustomerPayeeId after successful update: {}", digitalCustomerPayeeId);

          return mapToPayeeResponse(
                    digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(digitalCustomerPayeeId).get());

     }

     private PayeeResponse mapToPayeeResponse(DigitalCustomerPayee payee) {
          return PayeeResponse.builder().payeeName(payee.getPayeeName())
                    .digitalCustomerProfileId(payee.getDigitalCustomerProfileId())
                    .payeeAccountNumber(payee.getPayeeAccountNumber())
                    .payeeNickname((payee.getPayeeNickName() != null) ? payee.getPayeeNickName() : null)
                    .payeeBankCode(payee.getPayeeBankCode()).payeeBankName(payee.getPayeeBankName())
                    .payeeReference((payee.getPayeeReference() != null) ? payee.getPayeeReference() : null)
                    .response(UdbConstants.CHANGES_UPDATED_SUCCESS)
                    .httpStatusCode(UdbConstants.PAYEE_SUCCESS_STATUS_200).build();

     }

}
