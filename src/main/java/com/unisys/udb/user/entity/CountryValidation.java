package com.unisys.udb.user.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryValidation {

    private String country;
    private String url;
    private boolean evidenceRequired;
}
