package com.unisys.udb.user.service.impl;

import com.unisys.udb.user.entity.MarketingNotificationTemplate;
import com.unisys.udb.user.repository.PromotionOfferTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public abstract class PromotionTemplateRepositoryImpl implements PromotionOfferTemplateRepository {
    private MongoTemplate mongoTemplate;

    @Override
    public MarketingNotificationTemplate findByMessageCode(String messageCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("message_code").is(messageCode));
        return mongoTemplate.findOne(query, MarketingNotificationTemplate.class);
    }

}