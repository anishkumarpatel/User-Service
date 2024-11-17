package com.unisys.udb.user.controller;

import static org.mockito.Mockito.when;

import com.unisys.udb.user.dto.response.UdbFileConfigResponse;
import com.unisys.udb.user.service.UdbFileConfigurationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {UdbFileConfigController.class})
@ExtendWith(SpringExtension.class)
class UdbFileConfigControllerTest {
    @Autowired
    private UdbFileConfigController udbFileConfigController;

    @MockBean
    private UdbFileConfigurationService udbFileConfigurationService;


    private static final long FILE_SIZE = 3L;

    private static final int FILE_FORMAT = 10;
    @Test
    void testGetUdbFileConfig() throws Exception {
        when(udbFileConfigurationService.getUdbFileConfig())
                .thenReturn(new UdbFileConfigResponse("Upload File Format", FILE_SIZE,  FILE_FORMAT));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/udbConfigurations");
        MockMvcBuilders.standaloneSetup(udbFileConfigController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"udbConfigParameters\":{\"uploadFileFormat\":\"Upload File Format\","
                                        + "\"uploadFileSize\":3,\"uploadNumberOfFiles"
                                        + "\":10}}"));
    }
}
