package com.ppp.api.subscription.controller;

import com.ppp.api.subscription.service.SubscriptionService;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscription", description = "Subscription APIs")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/{petId}/subscriptions")
    private ResponseEntity<Void> subscribe(@PathVariable Long petId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails){
        subscriptionService.subscribeOrUnsubscribe(petId, principalDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
