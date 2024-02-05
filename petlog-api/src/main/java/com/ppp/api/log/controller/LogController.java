package com.ppp.api.log.controller;

import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.service.LogService;
import com.ppp.common.security.PrincipalDetails;
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

    @PostMapping
    private ResponseEntity<Void> createLog(@PathVariable Long petId,
                                           @Valid @RequestBody LogRequest request,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.createLog(principalDetails.getUser(), petId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{logId}")
    private ResponseEntity<Void> updateLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @Valid @RequestBody LogRequest request,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.updateLog(principalDetails.getUser(), petId, logId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{logId}")
    private ResponseEntity<Void> deleteLog(@PathVariable Long petId,
                                           @PathVariable Long logId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logService.deleteLog(principalDetails.getUser(), petId, logId);
        return ResponseEntity.ok().build();
    }
}
