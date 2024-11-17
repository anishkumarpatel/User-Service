package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.MarketingNotificationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionOfferTemplateRepository extends MongoRepository<MarketingNotificationTemplate, String> {
    MarketingNotificationTemplate findByMessageCode(String messageCode);
}