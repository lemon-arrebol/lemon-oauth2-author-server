package com.lemon.oauth2.service.impl;

import com.lemon.oauth2.domain.CustomUserDetail;
import com.lemon.oauth2.entity.User;
import com.lemon.oauth2.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lemon
 * @description 从数据库查询用户信息
 * @date 2020-05-05 21:16
 */
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (StringUtils.isBlank(userName)) {
            throw new UsernameNotFoundException("Account is not allowed to be empty");
        }

        User user = userRepository.findUserByAccount(userName);

        if (user != null) {
            CustomUserDetail customUserDetail = new CustomUserDetail();
            customUserDetail.setUsername(user.getAccount());
            customUserDetail.setPassword(this.passwordEncoder.encode(user.getPassword()));
            List<GrantedAuthority> list = AuthorityUtils.createAuthorityList(user.getRole().getRole());
            customUserDetail.setAuthorities(list);
            return customUserDetail;
        } else {
            throw new UsernameNotFoundException(String.format("Account %s does not exist", userName));
        }
    }
}
