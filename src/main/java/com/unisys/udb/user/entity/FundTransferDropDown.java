package com.unisys.udb.user.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "digital_app_dropdowns")
public class FundTransferDropDown {


    @Field("locale_language_code")
    private String localeLanguageCode;

    @Field("dropdowns")
    private List<Dropdown> dropdowns;

    @Data
    public static class Dropdown {
        @Field("dropdown_id")
        private String dropdownId;

        @Field("dropdown_values")
        private List<DropdownValue> dropdownValues;

        @Data
        public static class DropdownValue {
            private String code;
            private String value;
        }
    }
}
