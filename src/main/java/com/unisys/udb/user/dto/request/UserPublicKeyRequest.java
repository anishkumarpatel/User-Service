package com.unisys.udb.user.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicKeyRequest {
    @NotNull
    private String deviceUUID;
    private String devicePublicKey;
    @NotNull
    private String biometricType;
    private Boolean biometricEnable = true;
}
