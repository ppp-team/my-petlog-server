package com.ppp.common.security;

import com.ppp.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class PrincipalDetails implements UserDetails {
    private User user;

    private Set<GrantedAuthority> grantedAuthorities;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Set<GrantedAuthority> grantedAuthorities) {
        this.user = user;
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = grantedAuthorities;
//        collection.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return user.getRole();
//            }
//        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}