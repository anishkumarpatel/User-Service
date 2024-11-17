package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.dto.response.PromotionOfferDto;
import com.unisys.udb.user.constants.UdbConstants;
import com.unisys.udb.user.dto.response.PromotionOfferResponse;
import com.unisys.udb.user.entity.MarketingNotificationTemplate;
import com.unisys.udb.user.exception.DigitalCustomerProfileIdNotFoundException;
import com.unisys.udb.user.repository.DigitalCustomerDeviceRepository;
import com.unisys.udb.user.repository.DigitalCustomerProfileRepository;
import com.unisys.udb.user.repository.PromotionOfferTemplateRepository;
import com.unisys.udb.user.service.PromotionOffers;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.UdbConstants.*;

@Service
@Slf4j
public class PromotionOffersImpl implements PromotionOffers {


    private final DigitalCustomerDeviceRepository deviceRepository;
    private final DigitalCustomerProfileRepository digitalCustomerProfileRepository;
    private final PromotionOfferTemplateRepository promotionTemplateRepository;
    @Value("${app.application_default_language}")
    private String marketingContentKey;

    @Autowired
    public PromotionOffersImpl(DigitalCustomerDeviceRepository deviceRepository,
                               DigitalCustomerProfileRepository digitalCustomerProfileRepository,
                               PromotionOfferTemplateRepository promotionTemplateRepository) {
        this.deviceRepository = deviceRepository;
        this.digitalCustomerProfileRepository = digitalCustomerProfileRepository;
        this.promotionTemplateRepository = promotionTemplateRepository;
    }

    @Override
    public Mono<List<PromotionOfferDto>> getPromotionOffers(UUID digitalCustomerProfileId) {
        Boolean isDigitalCustomerProfileId = digitalCustomerProfileRepository
                .existsByDigitalCustomerProfileId(digitalCustomerProfileId);
        List<String> errorCode = new ArrayList<>();
        errorCode.add(NOT_FOUND_ERROR_CODE);
        List<String> params = new ArrayList<>();
        params.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern(UdbConstants.DATE_FORMAT)));
        if (Boolean.FALSE.equals(isDigitalCustomerProfileId)) {
            return Mono.error(new DigitalCustomerProfileIdNotFoundException(errorCode,
                    HttpStatus.NOT_FOUND,
                    FAILURE,
                    NOT_FOUND_ERROR_MESSAGE + digitalCustomerProfileId,
                    params));
        }
        List<PromotionOfferResponse> promotionOfferResponses = deviceRepository.findByPromotionOffers(
                digitalCustomerProfileId).stream().map(this::mapToPromotionOfferResponse).toList();
        if (promotionOfferResponses.isEmpty()) {
            PromotionOfferDto.MarketingContent content1 = new PromotionOfferDto.MarketingContent("Unibank "
                    + "Regular Saver", "Maximize Your Savings with UniBank Regular Saver",
                    "https://udb-customer-management.s3.us-east-2.amazonaws.com/marketing_image.jpg",
                    LocalDateTime.now().toString(), "gunti",
                    LocalDateTime.now().toString(), "Gunti",
                    "encodedImage");
            PromotionOfferDto promotionOfferDto = new PromotionOfferDto();
            promotionOfferDto.setPromotion(List.of(content1));
            return Mono.just(List.of(promotionOfferDto));
        }
        return Mono.just(getPromotionOffers(promotionOfferResponses));
    }

    public List<PromotionOfferDto> getPromotionOffers(List<PromotionOfferResponse> promotionOfferResponses) {
        List<PromotionOfferDto> promotionOfferDtos = new ArrayList<>();
        for (PromotionOfferResponse promotionOfferResponse : promotionOfferResponses) {
            MarketingNotificationTemplate marketingNotificationTemplate = promotionTemplateRepository
                    .findByMessageCode(String.valueOf(promotionOfferResponse.getMarketingRefId()));
            PromotionOfferDto promotionOfferDto = transformToPromotionOfferDto(marketingNotificationTemplate);
            promotionOfferDtos.add(promotionOfferDto);
        }
        return promotionOfferDtos;
    }


    private PromotionOfferDto transformToPromotionOfferDto(
            MarketingNotificationTemplate marketingNotificationTemplate) {
        PromotionOfferDto promotionOfferDto = new PromotionOfferDto();
        List<PromotionOfferDto.MarketingContent> marketingContents = new ArrayList<>();
        if (marketingNotificationTemplate != null
                && marketingNotificationTemplate.getMessageContent() != null) {
            String htmlContent = marketingNotificationTemplate.getMessageContent().get(marketingContentKey);
            String fileContent = marketingNotificationTemplate.getFileContent();
            if (htmlContent != null) {
                Document doc = Jsoup.parse(htmlContent);
                String title = doc.title();
                String body = doc.body().text();
                Elements imgElements = doc.select("a[href]");
                String imageUrl = imgElements.isEmpty() ? "" : imgElements.attr("href");
                String createdAt = marketingNotificationTemplate.getCreatedAt();
                String createdBy = marketingNotificationTemplate.getCreatedBy();
                String updatedAt = marketingNotificationTemplate.getUpdatedAt();
                String updatedBy = marketingNotificationTemplate.getUpdatedBy();
                marketingContents.add(new PromotionOfferDto.MarketingContent(title,
                        body, imageUrl, createdAt, createdBy, updatedAt, updatedBy, fileContent));
            }
        }
        promotionOfferDto.setPromotion(marketingContents);
        return promotionOfferDto;
    }

    public PromotionOfferResponse mapToPromotionOfferResponse(Object[] result) {
        Integer marketingRefId = (Integer) result[0];
        return new PromotionOfferResponse(marketingRefId);
    }
}
