package com.ppp.common.security;

import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    final UserRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("######### CustomUserDetailsService-username: {}",username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Optional<User> user =  memberRepository.findByEmail(username);

        if(user.isPresent()) {
            log.debug("######### CustomUserDetailsService-username: 유저생성");

            User authenUser = user.get();
            grantedAuthorities.add(new SimpleGrantedAuthority(authenUser.getRole().getType()));
            return new PrincipalDetails(authenUser, grantedAuthorities);
        }
        return null;
    }
}
