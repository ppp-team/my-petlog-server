package com.ppp.api.diary.service;

import com.ppp.api.diary.dto.response.DiaryFeedResponse;
import com.ppp.api.subscription.dto.transfer.SubscriptionInfoDto;
import com.ppp.api.subscription.service.SubscriptionService;
import com.ppp.domain.diary.repository.DiaryQuerydslRepository;
import com.ppp.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DiaryFeedService {

    private final DiaryQuerydslRepository diaryQuerydslRepository;
    private final SubscriptionService subscriptionService;
    private final DiaryCommentRedisService diaryCommentRedisService;
    private final DiaryRedisService diaryRedisService;

    public Set<DiaryFeedResponse> retrieveDiaryFeed(User user, int page, int size) {
        SubscriptionInfoDto subscriptionInfo = subscriptionService.getUsersSubscriptionInfo(user.getId());
        List<DiaryFeedResponse> subscribedPetsDiaries = diaryQuerydslRepository
                .findSubscribedPetsDiariesBySubscription(subscriptionInfo.subscribedPetIds(), PageRequest.of(page, size / 2))
                .stream().map(dto -> DiaryFeedResponse.from(dto,
                        diaryCommentRedisService.getDiaryCommentCountByDiaryId(dto.getDiaryId()),
                        diaryRedisService.isLikeExistByDiaryIdAndUserId(dto.getDiaryId(), user.getId()),
                        diaryRedisService.getLikeCountByDiaryId(dto.getDiaryId()),
                        true
                )).toList();
        List<DiaryFeedResponse> randomPetDiaries = diaryQuerydslRepository.findRandomPetsDiaries(subscriptionInfo.blockedPetIds(), PageRequest.of(page, size - size / 2))
                .stream().map(dto -> DiaryFeedResponse.from(dto,
                        diaryCommentRedisService.getDiaryCommentCountByDiaryId(dto.getDiaryId()),
                        diaryRedisService.isLikeExistByDiaryIdAndUserId(dto.getDiaryId(), user.getId()),
                        diaryRedisService.getLikeCountByDiaryId(dto.getDiaryId()),
                        subscriptionInfo.subscribedPetIds().contains(dto.getPetId())
                )).toList();
        Set<DiaryFeedResponse> response = new HashSet<>(subscribedPetsDiaries);
        response.addAll(randomPetDiaries);
        return response;
    }
}
