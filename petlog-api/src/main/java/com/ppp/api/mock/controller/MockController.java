package com.ppp.api.mock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MockController {

    @GetMapping("/testtest")
    public ResponseEntity<String> test() throws Exception {

//        throw new MockException(ErrorCode.MEMBER_NOT_WRITER);
        throw new Exception("글로벌 예외처리");
    }
}
