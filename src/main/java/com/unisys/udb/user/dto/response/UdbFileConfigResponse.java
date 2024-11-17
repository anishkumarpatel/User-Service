package com.unisys.udb.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UdbFileConfigResponse {

    private String uploadFileFormat;
    private long uploadFileSize;
    private int uploadNumberOfFiles;



}
