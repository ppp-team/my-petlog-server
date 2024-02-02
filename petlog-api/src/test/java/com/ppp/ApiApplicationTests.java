package com.ppp;

import com.ppp.common.config.JasyptConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ApiApplicationTests {

    @MockBean(JasyptConfig.class)
    private JasyptConfig jasyptConfig;

    @Test
    void contextLoads() {
    }

}
