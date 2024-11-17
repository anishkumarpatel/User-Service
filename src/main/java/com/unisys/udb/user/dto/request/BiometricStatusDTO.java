package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BiometricStatusDTO {

    @NotNull
    private Boolean faceId;
    @NotNull
    private Boolean touchId;

}
