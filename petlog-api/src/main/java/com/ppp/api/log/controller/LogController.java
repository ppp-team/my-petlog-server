package com.ppp.api.log.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.service.LogService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets/{petId}/logs")
public class LogController {
    private final LogService logService;

    @Operation(summary = "기록 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 반려동물 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 유저 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping
    private ResponseEntity<Void> createLog(@PathVariable Long petId,
                                           @Valid @RequestBody LogRequest request,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.createLog(principalDetails.getUser(), petId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 유저 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PutMapping(value = "/{logId}")
    private ResponseEntity<Void> updateLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @Valid @RequestBody LogRequest request,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.updateLog(principalDetails.getUser(), petId, logId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 유저 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @DeleteMapping(value = "/{logId}")
    private ResponseEntity<Void> deleteLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.deleteLog(principalDetails.getUser(), petId, logId);
        return ResponseEntity.ok().build();
    }
}
