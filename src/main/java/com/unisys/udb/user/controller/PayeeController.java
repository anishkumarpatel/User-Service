package com.unisys.udb.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPayeeResponse;
import com.unisys.udb.user.dto.response.PayeeFailedResponse;
import com.unisys.udb.user.dto.response.PayeeResponse;
import com.unisys.udb.user.entity.FundTransferDropDown;

import com.unisys.udb.user.service.DigitalPayeeService;
import com.unisys.udb.user.service.impl.UserInfoServiceImpl;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;
import com.unisys.udb.user.utils.schemavalidation.PayeeSchemaValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payee")
public class PayeeController {

     private final DigitalPayeeService digitalPayeeService;
     private final UserInfoServiceImpl userInfoService;
     private final PayeeSchemaValidation payeeSchemaValidation;
     private final NotificationUtil notificationUtil;

     @PostMapping("/addPayee")
     public ResponseEntity<PayeeResponse> addPayee(@RequestBody PayeeRequest payeeRequest)
               throws JsonProcessingException {
          PayeeResponse payeeResponse = new PayeeResponse();
          log.debug("Received request to add a payee  for the digital customer profile id  :{}",
                    payeeRequest.getDigitalCustomerProfileId());
          payeeSchemaValidation.validateSchema(payeeRequest);
          digitalPayeeService.createPayee(payeeRequest);
          log.debug("create Payee::End");
          BeanUtils.copyProperties(payeeRequest, payeeResponse);
          payeeResponse.setResponse(UdbConstants.PAYEE_CREATE_MESSAGE_200);
          payeeResponse.setHttpStatusCode(UdbConstants.PAYEE_SUCCESS_STATUS_200);
          return ResponseEntity.status(HttpStatus.OK).body(payeeResponse);
     }

     @GetMapping("/getAllPayees")
     public ResponseEntity<List<DigitalCustomerPayeeResponse>> getAllPayees(
               @RequestParam(required = true) final UUID digitalCustomerProfileId,
               @RequestParam(required = false) final String sortBy) {
          log.info("Inside the getAllPayees method of the user service backend service");

          return ResponseEntity.ok().body(digitalPayeeService.getAllPayees(digitalCustomerProfileId, sortBy));

     }

     @PutMapping("/updatePayee/{digitalCustomerPayeeId}")
     public ResponseEntity<Object> updatePayeeDetails(@PathVariable Integer digitalCustomerPayeeId,
               @RequestParam(required = false) String payeeNickName,
               @RequestParam(required = false) String payeeReference, @RequestParam(required = true) String userName) {

          log.info("Updating the payee details", digitalCustomerPayeeId, payeeNickName);

          try {
               digitalPayeeService.updatePayeeDetails(digitalCustomerPayeeId, payeeNickName, payeeReference, userName);
               log.info("updated the payee details successfully" + digitalCustomerPayeeId);

               return ResponseEntity.status(HttpStatus.OK).body(digitalPayeeService.getPayee(digitalCustomerPayeeId));
          } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PayeeFailedResponse.builder()
                         .response(UdbConstants.NO_PAY_ID).httpStatusCode(HttpStatus.NOT_FOUND.value()).build());
          }

     }

     @GetMapping("/viewPayeeDetails/{digitalCustomerPayeeId}")
     public ResponseEntity<Object> viewPayeeDetails(@PathVariable Integer digitalCustomerPayeeId) {

          log.info("Inside the viewPayeeDetails method of the user service");
          try {
               return ResponseEntity.ok().body(digitalPayeeService.getPayeeData(digitalCustomerPayeeId));
          } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PayeeFailedResponse.builder()
                         .response(UdbConstants.NO_PAY_ID_GET).httpStatusCode(HttpStatus.NOT_FOUND.value()).build());
          }

     }

     @DeleteMapping("/delete")
     public ResponseEntity<List<DigitalCustomerPayeeResponse>> deletePayee(
               @RequestBody List<Integer> digitalCustomerPayeeId) {
          log.debug("deletePayee::Start");
          log.debug("Received request to delete a payees  for all payee ids :{}", digitalCustomerPayeeId);
          List<DigitalCustomerPayeeResponse> list = digitalPayeeService.deletePayeeServe(digitalCustomerPayeeId);
          log.debug("deletePayee::End");
          return ResponseEntity.status(HttpStatus.OK).body(list);
     }

     @GetMapping("/fund-transfer/options/{languageCode}")
     public ResponseEntity<List<FundTransferDropDown>> getFundTransferOptions(@PathVariable String languageCode) {
          log.info("Received request for to get fund transfer options");
          return ResponseEntity.ok(digitalPayeeService.getFundTransferDropdownValues(languageCode));

     }

     @PostMapping(path = "/transfer")
     public ResponseEntity<Void> sendNotification1(@RequestParam final UUID digitalCustomerProfileId,
               @RequestParam final String digitalUserName, @RequestParam final String eventSource,
               @RequestParam final String activity, @RequestParam final String templateName,
               @RequestParam final String languagePreference, @RequestParam final String deviceId) {
          Map<String, String> requiredFieldsMap = notificationUtil.prepareRequiredFieldsMap(digitalCustomerProfileId,
                    digitalUserName, eventSource, activity, templateName, languagePreference);
          notificationUtil.sendNotification(requiredFieldsMap, new HashMap<>());
          return ResponseEntity.ok().build();
     }
}
