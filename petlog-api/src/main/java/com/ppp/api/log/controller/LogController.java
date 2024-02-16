package com.ppp.api.log.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.dto.response.LogCalenderResponse;
import com.ppp.api.log.dto.response.LogDetailResponse;
import com.ppp.api.log.dto.response.LogGroupByDateResponse;
import com.ppp.api.log.service.LogService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Log", description = "Log APIs")
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
            @ApiResponse(responseCode = "404", description = "일치하는 반려동물 없음, 일치하는 유저 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
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
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음, 일치하는 유저 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PutMapping(value = "/{logId}")
    private ResponseEntity<Void> updateLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @Valid @RequestBody LogRequest request,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.updateLog(principalDetails.getUser(), petId, logId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @DeleteMapping(value = "/{logId}")
    private ResponseEntity<Void> deleteLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.deleteLog(principalDetails.getUser(), petId, logId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LogDetailResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping(value = "/{logId}")
    private ResponseEntity<LogDetailResponse> displayLog(@PathVariable Long petId,
                                                         @PathVariable Long logId,
                                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(logService.displayLog(principalDetails.getUser(), petId, logId));
    }

    @Operation(summary = "기록 날짜별 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LogGroupByDateResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "날짜가 적합하지 않음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping
    private ResponseEntity<LogGroupByDateResponse> displayLogsByDate(@PathVariable Long petId,
                                                                     @RequestParam int year,
                                                                     @RequestParam int month,
                                                                     @RequestParam int day,
                                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(logService.displayLogsByDate(principalDetails.getUser(), petId, year, month, day));
    }

    @Operation(summary = "기록 해야할 일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = LogGroupByDateResponse.class)))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping(value = "/task")
    private ResponseEntity<Slice<LogGroupByDateResponse>> displayLogsToDo(@PathVariable Long petId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size,
                                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(logService.displayLogsToDo(principalDetails.getUser(), petId, page, size));
    }

    @Operation(summary = "기록 태스크 완료 / 미완료 체크")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 기록 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping(value = "/{logId}/check")
    private ResponseEntity<Void> checkComplete(@PathVariable Long petId,
                                               @PathVariable Long logId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.checkComplete(principalDetails.getUser(), petId, logId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 날짜별 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LogCalenderResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "날짜가 적합하지 않음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping(value = "/calender")
    @Validated
    private ResponseEntity<LogCalenderResponse> displayLogRecordedDayByTheMonth(@PathVariable Long petId,
                                                                                @RequestParam int year,
                                                                                @RequestParam int month,
                                                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(logService.displayLogRecordedDayByTheMonth(principalDetails.getUser(), petId, year, month));
    }
}
