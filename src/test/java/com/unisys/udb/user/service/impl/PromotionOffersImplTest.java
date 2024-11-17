package com.unisys.udb.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unisys.udb.user.dto.response.PromotionOfferDto;
import com.unisys.udb.user.dto.response.PromotionOfferResponse;
import com.unisys.udb.user.entity.MarketingNotificationTemplate;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.PromotionOfferTemplateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ContextConfiguration(classes = {PromotionOffersImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class PromotionOffersImplTest {
    @MockBean
    private DigitalCustomerDeviceRepository digitalCustomerDeviceRepository;

    @MockBean
    private DigitalCustomerProfileRepository digitalCustomerProfileRepository;

    @MockBean
    private PromotionOfferTemplateRepository promotionOfferTemplateRepository;

    @Autowired
    private PromotionOffersImpl promotionOffersImpl;


    @Test
    void testGetPromotionOffers() {
        assertTrue(promotionOffersImpl.getPromotionOffers(new ArrayList<>()).isEmpty());
    }


    @Test
    void testGetPromotionOffers2() {
        MarketingNotificationTemplate marketingNotificationTemplate = new MarketingNotificationTemplate();
        marketingNotificationTemplate.setCreatedAt("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationTemplate.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationTemplate.setFileContent("Not all who wander are lost");
        marketingNotificationTemplate.setId("42");
        marketingNotificationTemplate.setMessageCode("Message Code");
        marketingNotificationTemplate.setMessageContent(new HashMap<>());
        marketingNotificationTemplate.setUpdatedAt("2020-03-01");
        marketingNotificationTemplate.setUpdatedBy("2020-03-01");
        when(promotionOfferTemplateRepository.findByMessageCode(Mockito.<String>any()))
                .thenReturn(marketingNotificationTemplate);

        ArrayList<PromotionOfferResponse> promotionOfferResponses = new ArrayList<>();
        promotionOfferResponses.add(new PromotionOfferResponse());

        List<PromotionOfferDto> actualPromotionOffers = promotionOffersImpl.getPromotionOffers(promotionOfferResponses);

        verify(promotionOfferTemplateRepository).findByMessageCode("null");
        assertEquals(1, actualPromotionOffers.size());
        assertTrue(actualPromotionOffers.get(0).getPromotion().isEmpty());
    }


    @Test
    void testGetPromotionOffers3() {
        MarketingNotificationTemplate marketingNotificationTemplate = new MarketingNotificationTemplate();
        marketingNotificationTemplate.setCreatedAt("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationTemplate.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        marketingNotificationTemplate.setFileContent("Not all who wander are lost");
        marketingNotificationTemplate.setId("42");
        marketingNotificationTemplate.setMessageCode("Message Code");
        marketingNotificationTemplate.setMessageContent(new HashMap<>());
        marketingNotificationTemplate.setUpdatedAt("2020-03-01");
        marketingNotificationTemplate.setUpdatedBy("2020-03-01");
        when(promotionOfferTemplateRepository.findByMessageCode(Mockito.<String>any()))
                .thenReturn(marketingNotificationTemplate);

        ArrayList<PromotionOfferResponse> promotionOfferResponses = new ArrayList<>();
        promotionOfferResponses.add(new PromotionOfferResponse());
        promotionOfferResponses.add(new PromotionOfferResponse());

        List<PromotionOfferDto> actualPromotionOffers = promotionOffersImpl.getPromotionOffers(promotionOfferResponses);

        verify(promotionOfferTemplateRepository, atLeast(1)).findByMessageCode("null");
        assertEquals(2, actualPromotionOffers.size());
        PromotionOfferDto getResult = actualPromotionOffers.get(0);
        assertTrue(getResult.getPromotion().isEmpty());
        assertEquals(getResult, actualPromotionOffers.get(1));
    }


    @Test
    void testGetPromotionOffers4() {
        ArrayList<String> errorCode = new ArrayList<>();
        when(promotionOfferTemplateRepository.findByMessageCode(Mockito.<String>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode,
                        HttpStatus.CONTINUE, "Response Type",
                        "An error occurred", new ArrayList<>()));

        ArrayList<PromotionOfferResponse> promotionOfferResponses = new ArrayList<>();
        promotionOfferResponses.add(new PromotionOfferResponse());

        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> promotionOffersImpl.getPromotionOffers(promotionOfferResponses));
        verify(promotionOfferTemplateRepository).findByMessageCode("null");
    }


    @Test
    void testGetPromotionOffers5() throws AssertionError {
        when(digitalCustomerDeviceRepository.findByPromotionOffers(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);

        StepVerifier.FirstStep<List<PromotionOfferDto>> createResult = StepVerifier
                .create(promotionOffersImpl.getPromotionOffers(UUID.randomUUID()));
        createResult.assertNext(l -> {
            List<PromotionOfferDto> promotionOfferDtoList = l;
            assertEquals(1, promotionOfferDtoList.size());
            List<PromotionOfferDto.MarketingContent> promotion = promotionOfferDtoList.get(0).getPromotion();
            assertEquals(1, promotion.size());
            PromotionOfferDto.MarketingContent getResult = promotion.get(0);
            assertEquals("Maximize Your Savings with UniBank Regular Saver", getResult.getBody());
            assertEquals("gunti", getResult.getCreatedBy());
            assertEquals("encodedImage", getResult.getEncodedImage());
            assertEquals("https://udb-customer-management.s3.us-east-2.amazonaws.com/marketing_image.jpg",
                    getResult.getImagePath());
            assertEquals("Unibank Regular Saver", getResult.getTitle());
            assertEquals("Gunti", getResult.getUpdatedBy());
            return;
        }).expectComplete().verify();
        verify(digitalCustomerDeviceRepository).findByPromotionOffers(isA(UUID.class));
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(isA(UUID.class));
    }


    @Test
    void testGetPromotionOffers6() {
        ArrayList<String> errorCode = new ArrayList<>();
        when(digitalCustomerDeviceRepository.findByPromotionOffers(Mockito.<UUID>any()))
                .thenThrow(new DigitalCustomerProfileIdNotFoundException(errorCode, HttpStatus.CONTINUE, "404",
                        "An error occurred", new ArrayList<>()));
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(true);

        UUID randomUUID = UUID.randomUUID();
        assertThrows(DigitalCustomerProfileIdNotFoundException.class,
                () -> promotionOffersImpl.getPromotionOffers(randomUUID));
        verify(digitalCustomerDeviceRepository).findByPromotionOffers(isA(UUID.class));
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(isA(UUID.class));
    }

    @Test
    void testGetPromotionOffers7() throws AssertionError {
        when(digitalCustomerProfileRepository.existsByDigitalCustomerProfileId(Mockito.<UUID>any())).thenReturn(false);

        StepVerifier.FirstStep<List<PromotionOfferDto>> createResult = StepVerifier
                .create(promotionOffersImpl.getPromotionOffers(UUID.randomUUID()));
        createResult.expectError().verify();
        verify(digitalCustomerProfileRepository).existsByDigitalCustomerProfileId(isA(UUID.class));
    }

    @Test
    void testMapToPromotionOfferResponse() {
        assertEquals(1,
                promotionOffersImpl.mapToPromotionOfferResponse(new
                        Object[]{1}).getMarketingRefId().intValue());
    }
}
