
package com.unisys.udb.user.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPayeeResponse;
import com.unisys.udb.user.dto.response.PayeeResponse;
import com.unisys.udb.user.entity.DigitalCustomerPayee;
import com.unisys.udb.user.entity.FundTransferDropDown;
import com.unisys.udb.user.exception.FundTransferValuesNotFound;
import com.unisys.udb.user.repository.DigitalCustomerPayeeRepository;
import com.unisys.udb.user.utils.dto.response.NotificationUtil;

@ContextConfiguration(classes = { DigitalPayeeServiceImpl.class })
@ExtendWith(SpringExtension.class)
class DigitalPayeeServiceImplTest {
     @MockBean
     private DigitalCustomerPayeeRepository digitalCustomerPayeeRepository;

     @Autowired
     private DigitalPayeeServiceImpl digitalPayeeServiceImpl;

     @MockBean
     private ModelMapper modelMapper;
     @Mock
     private final DigitalCustomerPayee digitalCustomerPayee = createPayeeDetailsObject();
     @MockBean
     private UserInfoServiceImpl userInfoService;
     @MockBean
     private NotificationUtil notificationUtil;

     @MockBean
     private MongoTemplate mongoTemplate;

     @BeforeEach
     public void setup() {
          MockitoAnnotations.openMocks(this);
     }

     public static final int YEAR = 100;

     /**
      * Method under test: {@link DigitalPayeeServiceImpl#createPayee(PayeeRequest)}
      */
     @Test
     void testCreatePayee() {
          // Arrange
          DigitalCustomerPayee digitalCustomerPaye = new DigitalCustomerPayee();
          digitalCustomerPaye.setDigitalCustomerPayeeId(1);
          digitalCustomerPaye.setDigitalCustomerProfileId(UUID.randomUUID());
          digitalCustomerPaye.setPayeeAccountNumber("42");
          digitalCustomerPaye.setPayeeBankCode("Payee Bank Code");
          digitalCustomerPaye.setPayeeBankName("Payee Bank Name");
          digitalCustomerPaye.setPayeeCreatedBy("Jan 1, 2020 8:00am GMT+0100");
          digitalCustomerPaye.setPayeeCreationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
          digitalCustomerPaye.setPayeeModificationDate(LocalDate.of(YEAR, 1, 1).atStartOfDay());
          digitalCustomerPaye.setPayeeModifiedBy("Jan 1, 2020 9:00am GMT+0100");
          digitalCustomerPaye.setPayeeName("Payee Name");
          digitalCustomerPaye.setPayeeNickName("Payee Nick Name");
          digitalCustomerPaye.setPayeeReference("Payee Reference");
          when(digitalCustomerPayeeRepository.save(Mockito.<DigitalCustomerPayee>any()))
                    .thenReturn(digitalCustomerPaye);
          digitalPayeeServiceImpl.createPayee(new PayeeRequest(UUID.randomUUID(), "Payee Name", "Payee Bank Name", "42",
                    "Payee Nickname", "Payee Bank Code", "Payee Reference"));

          // Assert
          verify(digitalCustomerPayeeRepository).save(Mockito.<DigitalCustomerPayee>any());
     }

     @Test
     void testGetAllPayees() throws DataAccessException {
          UUID profileId = UUID.randomUUID();
          String sortBy = "name";
          List<DigitalCustomerPayee> payeeData = createPayeeDetailsObjectList();
          when(digitalCustomerPayeeRepository.findByDigitalCustomerProfileIdOrderByPayeeNickName(profileId))
                    .thenReturn(payeeData);

          List<DigitalCustomerPayeeResponse> responses = digitalPayeeServiceImpl.getAllPayees(profileId, sortBy);

          verify(digitalCustomerPayeeRepository).findByDigitalCustomerProfileIdOrderByPayeeNickName(profileId);
          assertEquals(1, responses.size());
          when(digitalCustomerPayeeRepository.findByDigitalCustomerProfileIdOrderByPayeeCreationDateDesc(profileId))
                    .thenReturn(payeeData);
          List<DigitalCustomerPayeeResponse> responseNoSort = digitalPayeeServiceImpl.getAllPayees(profileId, null);
          assertEquals(1, responseNoSort.size());
          sortBy = "creationDate";
          when(digitalCustomerPayeeRepository.findByDigitalCustomerProfileIdOrderByPayeeCreationDateDesc(profileId))
                    .thenReturn(payeeData);
          List<DigitalCustomerPayeeResponse> responses2 = digitalPayeeServiceImpl.getAllPayees(profileId, sortBy);

          assertEquals(1, responses2.size());

     }

     public List<DigitalCustomerPayee> createPayeeDetailsObjectList() {
          List<DigitalCustomerPayee> response = new ArrayList<DigitalCustomerPayee>();
          DigitalCustomerPayee payee = new DigitalCustomerPayee();
          payee.setDigitalCustomerPayeeId(Integer.valueOf(1));
          payee.setDigitalCustomerProfileId(UUID.randomUUID());
          payee.setPayeeName("ABC");
          payee.setPayeeBankName("MMM");
          payee.setPayeeAccountNumber("111795099111");
          payee.setPayeeNickName("MCB");
          payee.setPayeeBankCode("554");
          payee.setPayeeReference("KPX");

          LocalDateTime dateTime = LocalDateTime.parse("2024-03-03 11:30:40",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

          payee.setPayeeCreationDate(dateTime);
          payee.setPayeeCreatedBy("HK");
          response.add(payee);
          return response;
     }

     @Test
     void testUpdateDigitalPayeeDetails() throws DataAccessException {
          Integer digitalPayeeId = 1;
          String payeeNickName = "PPP";
          String payeeReference = "YYY";
          when(digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(anyInt()))
                    .thenReturn(java.util.Optional.of(digitalCustomerPayee));

          boolean result = digitalPayeeServiceImpl.updatePayeeDetails(digitalPayeeId, payeeNickName, payeeReference,
                    "BBB");
          assertTrue(result);
          boolean result1 = digitalPayeeServiceImpl.updatePayeeDetails(digitalPayeeId, payeeNickName, null, "BBB");
          assertTrue(result1);
          boolean result3 = digitalPayeeServiceImpl.updatePayeeDetails(digitalPayeeId, null, null, "BBB");
          assertFalse(result3);
          boolean result4 = digitalPayeeServiceImpl.updatePayeeDetails(digitalPayeeId, null, "KK", "BBB");
          assertTrue(result4);

     }

     public DigitalCustomerPayee createPayeeDetailsObject() {

          DigitalCustomerPayee payee = new DigitalCustomerPayee();
          payee.setDigitalCustomerPayeeId(Integer.valueOf(1));
          payee.setDigitalCustomerProfileId(UUID.randomUUID());
          payee.setPayeeName("ABC");
          payee.setPayeeBankName("MMM");
          payee.setPayeeAccountNumber("111795099111");
          payee.setPayeeNickName("MCB");
          payee.setPayeeBankCode("554");
          payee.setPayeeReference("KPX");

          LocalDateTime dateTime = LocalDateTime.parse("2024-03-03 11:30:40",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

          payee.setPayeeCreationDate(dateTime);
          payee.setPayeeCreatedBy("HK");

          return payee;
     }

     @Test
     void testGetPayeeTrxDetails() throws DataAccessException {
          Integer digitalPayeeId = 1;

          DigitalCustomerPayee payee = createPayeeDetailsObject();
          when(digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(digitalPayeeId))
                    .thenReturn(Optional.of(payee));
          DigitalCustomerPayeeResponse response = digitalPayeeServiceImpl.getPayeeData(digitalPayeeId);
          assertNotNull(response);

     }

     @Test
     void testDeletePayee() {
          List<Integer> digitalCustomerPayeeIds = Arrays.asList(1, 2);
          List<DigitalCustomerPayeeResponse> mockPayeeList = new ArrayList<>();
          // Mock the getAllPayees method
          when(digitalPayeeServiceImpl.getAllPayees(UUID.randomUUID(), any())).thenReturn(mockPayeeList);

          // Mock the existsById and findById methods
          when(digitalCustomerPayeeRepository.existsById(anyInt())).thenReturn(true);
          when(digitalCustomerPayeeRepository.findById(anyInt())).thenReturn(Optional.of(digitalCustomerPayee));
          doNothing().when(digitalCustomerPayeeRepository).deleteAllById(digitalCustomerPayeeIds);

          // Execute the method under test
          List<DigitalCustomerPayeeResponse> result = digitalPayeeServiceImpl.deletePayeeServe(digitalCustomerPayeeIds);

          digitalCustomerPayeeRepository.deleteAllById(digitalCustomerPayeeIds);
          verify(digitalCustomerPayeeRepository, times(1)).deleteAllById(digitalCustomerPayeeIds);
          assertTrue(result.isEmpty());
          assertEquals(mockPayeeList.size(), result.size());
     }

     @Test
     void testDeletePayeeServePayeeWithProfileIdSuccess() {
          // Arrange
          UUID randomUUIDResult = UUID.randomUUID();
          DigitalCustomerPayee digitalCustomer = mock(DigitalCustomerPayee.class);
          when(digitalCustomer.getDigitalCustomerProfileId()).thenReturn(randomUUIDResult);

          List<DigitalCustomerPayee> digitalCustomerPayeeList = Collections.singletonList(digitalCustomer);
          when(digitalCustomerPayeeRepository.findByDigitalCustomerProfileId(Mockito.<UUID>any()))
                    .thenReturn(digitalCustomerPayeeList);
          digitalPayeeServiceImpl.deletePayeeServe(new ArrayList<>());
          verify(notificationUtil).sendNotification(isA(Map.class), isA(Map.class));
          verify(digitalCustomerPayeeRepository).deleteAllById(isA(Iterable.class));

     }

     @Test
     void testDeletePayeeServePayeeWithProfileId1() {
          // Arrange
          UUID randomUUIDResult = UUID.randomUUID();
          DigitalCustomerPayee digitalCustomer = mock(DigitalCustomerPayee.class);
          when(digitalCustomer.getDigitalCustomerProfileId()).thenReturn(randomUUIDResult);

          List<DigitalCustomerPayeeResponse> responses = new ArrayList<>();

          when(digitalPayeeServiceImpl.getAllPayees(randomUUIDResult, anyString())).thenReturn(responses);

          List<Integer> digitalCustomerDeleteLIds = Collections.singletonList(1);

          doNothing().when(digitalCustomerPayeeRepository).deleteAllById(any(Iterable.class));
          doNothing().when(notificationUtil).sendNotification(anyMap(), anyMap());

          // Act
          List<DigitalCustomerPayeeResponse> result = digitalPayeeServiceImpl
                    .deletePayeeServe(digitalCustomerDeleteLIds);

          assertEquals(responses, result);
          List<Integer> nonExistingPayeeIds = Collections.singletonList(1);
          List<DigitalCustomerPayeeResponse> nonDeletedResponses = digitalPayeeServiceImpl
                    .deletePayeeServe(nonExistingPayeeIds);
          assertEquals(responses, nonDeletedResponses); // It should return the existing responses

          // Case where notification fails
          doThrow(new RuntimeException("Notification failure")).when(notificationUtil).sendNotification(anyMap(),
                    anyMap());
          result = digitalPayeeServiceImpl.deletePayeeServe(digitalCustomerDeleteLIds);
          // The result should still be the same, but with a logged error
          assertEquals(responses, result);

     }

     @Test
     void testDeletePayeeServePayeeWithProfileId() {
          // Arrange
          UUID randomUUIDResult = UUID.randomUUID();
          DigitalCustomerPayee digitalCustomer = mock(DigitalCustomerPayee.class);
          when(digitalCustomer.getDigitalCustomerProfileId()).thenReturn(randomUUIDResult);
          List<DigitalCustomerPayeeResponse> responses = new ArrayList<>();

          List<DigitalCustomerPayee> digitalCustomerPayeeList = Collections.singletonList(digitalCustomer);
          when(digitalCustomerPayeeRepository.findByDigitalCustomerProfileId(any(UUID.class)))
                    .thenReturn(digitalCustomerPayeeList);

          List<Integer> digitalCustomerDeleteLIds = new ArrayList<>();
          digitalCustomerDeleteLIds.add(1);

          // Act

          assertEquals(responses, digitalPayeeServiceImpl.deletePayeeServe(digitalCustomerDeleteLIds));
     }

     @Test
     void testGetFundTransferDropdownValues() {
          // Arrange
          when(mongoTemplate.find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any()))
                    .thenReturn(new ArrayList<>());

          // Act and Assert
          assertThrows(FundTransferValuesNotFound.class,
                    () -> digitalPayeeServiceImpl.getFundTransferDropdownValues("en"));
          verify(mongoTemplate).find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any());
     }

     /**
      * Method under test:
      * {@link DigitalPayeeServiceImpl#getFundTransferDropdownValues(String)}
      */
     @Test
     void testGetFundTransferDropdownValues2() {
          // Arrange
          FundTransferDropDown fundTransferDropDown = new FundTransferDropDown();
          fundTransferDropDown.setDropdowns(new ArrayList<>());
          fundTransferDropDown.setLocaleLanguageCode("en");

          ArrayList<FundTransferDropDown> fundTransferDropDownList = new ArrayList<>();
          fundTransferDropDownList.add(fundTransferDropDown);
          when(mongoTemplate.find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any()))
                    .thenReturn(fundTransferDropDownList);

          // Act
          List<FundTransferDropDown> actualFundTransferDropdownValues = digitalPayeeServiceImpl
                    .getFundTransferDropdownValues("en");

          // Assert
          verify(mongoTemplate).find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any());
          assertEquals(1, actualFundTransferDropdownValues.size());
          assertSame(fundTransferDropDownList, actualFundTransferDropdownValues);
     }

     /**
      * Method under test:
      * {@link DigitalPayeeServiceImpl#getFundTransferDropdownValues(String)}
      */
     @Test
     void testGetFundTransferDropdownValues3() {
          // Arrange
          when(mongoTemplate.find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any()))
                    .thenThrow(new FundTransferValuesNotFound("Not all who wander are lost"));

          // Act and Assert
          assertThrows(FundTransferValuesNotFound.class,
                    () -> digitalPayeeServiceImpl.getFundTransferDropdownValues("en"));
          verify(mongoTemplate).find(Mockito.<Query>any(), Mockito.<Class<FundTransferDropDown>>any());
     }

     @Test
     void testGetPayeeSuccess() {
          Integer digitalCustomerPayeeId = 1;
          DigitalCustomerPayee payee = new DigitalCustomerPayee();
          payee.setPayeeName("John Doe");
          payee.setDigitalCustomerProfileId(UUID.randomUUID());
          payee.setPayeeAccountNumber("123456789");
          payee.setPayeeNickName("John");
          payee.setPayeeBankCode("BANK123");
          payee.setPayeeBankName("Bank Name");
          payee.setPayeeReference("Ref123");

          when(digitalCustomerPayeeRepository.findByDigitalCustomerPayeeId(digitalCustomerPayeeId))
                    .thenReturn(Optional.of(payee));

          PayeeResponse response = digitalPayeeServiceImpl.getPayee(digitalCustomerPayeeId);

          assertNotNull(response);
          assertEquals("John Doe", response.getPayeeName());

          assertEquals("123456789", response.getPayeeAccountNumber());
          assertEquals("John", response.getPayeeNickname());
          assertEquals("BANK123", response.getPayeeBankCode());
          assertEquals("Bank Name", response.getPayeeBankName());
          assertEquals("Ref123", response.getPayeeReference());
          assertEquals(UdbConstants.CHANGES_UPDATED_SUCCESS, response.getResponse());
          assertEquals(UdbConstants.PAYEE_SUCCESS_STATUS_200, response.getHttpStatusCode());
     }
}