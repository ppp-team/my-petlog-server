package com.ppp.api.test;

import com.ppp.common.security.PrincipalDetails;
import com.ppp.domain.user.User;
import com.ppp.domain.user.constant.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private static final User user = User.builder()
            .id("abcd1234")
            .isDeleted(false)
            .email("abcd@gmail.com")
            .nickname("nickname")
            .role(Role.USER)
            .build();

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new PrincipalDetails(user), "", Stream.of(Role.USER.name()).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        context.setAuthentication(authentication);
        return context;
    }
}
