package com.unisys.udb.user.service;

import com.unisys.udb.user.dto.response.PromotionOfferDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public interface PromotionOffers {

    Mono<List<PromotionOfferDto>> getPromotionOffers(UUID digitalCustomerProfileId);
}
