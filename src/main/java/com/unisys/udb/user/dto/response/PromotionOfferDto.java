package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionOfferDto {
    private List<MarketingContent> promotion;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class MarketingContent {

        private String title;
        private String body;
        private String imagePath;
        private String createdAt;
        private String createdBy;
        private String updatedAt;
        private String updatedBy;
        private String encodedImage;

    }

}
