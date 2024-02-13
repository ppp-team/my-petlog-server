package com.ppp;

import com.ppp.common.client.FfmpegClient;
import com.ppp.common.config.FfmpegConfig;
import com.ppp.common.config.JasyptConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ApiApplicationTests {

    @MockBean
    private JasyptConfig jasyptConfig;

    @MockBean
    private FfmpegConfig ffmpegConfig;

    @MockBean
    private FfmpegClient ffmpegClient;

    @Test
    void contextLoads() {
    }

}
