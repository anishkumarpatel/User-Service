package com.unisys.udb.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExpiryDTO {
        @NotEmpty
        private String username;   // The username of the user
        @NotEmpty
        private String updateType; // Either "password" or "pin"
}
