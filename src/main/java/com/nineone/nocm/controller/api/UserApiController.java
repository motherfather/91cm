package com.nineone.nocm.controller.api;


import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.nineone.nocm.domain.Authorities;
import com.nineone.nocm.domain.enums.Role;
import com.nineone.nocm.repository.UserAuthoritiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nineone.nocm.annotation.Socialuser;
import com.nineone.nocm.domain.User;
import com.nineone.nocm.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthoritiesRepository authoritiesRepository;

    @RequestMapping("/list")
    public List<User> userList() {
        return userService.getAllUserList();
    }

    @RequestMapping("/admin/userList")
    public List<HashMap> rolesUserList(@Socialuser User user){
        return authoritiesRepository.getRoleUserList();
    }


    @RequestMapping(value = "/login")
    public boolean userInit(@Socialuser User user) {
        if (user != null) {
            return user.getPhone() != null ? true : false;
        } else {
            return false;
        }
    }

    @PostMapping("/update")
    public boolean userInfoUpdate(@RequestBody User user) {
        if (userService.userinfoUpdate(user)) {
            messagingTemplate.convertAndSend("/sub/sync/info", "userList");
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping(value = "/getsession")
    public User getSessionUser(@Socialuser User user) {
        return user;
    }

    @RequestMapping(value = "/info")
    public ResponseEntity<?> userInfo(@Socialuser User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public boolean signup(@RequestBody User user, Authentication authentication, HttpSession httpsession) {
        DefaultOAuth2User oauth2user = (DefaultOAuth2User) authentication.getPrincipal();
        if (userService.insertUser(user, oauth2user, httpsession)) {
            log.info(user.getEmail());
            authoritiesRepository.insertAuthority(Authorities.builder()
                    .member_email(user.getEmail())
                    .roles_authority("ROLE_ANON")
                    .build());
            messagingTemplate.convertAndSend("/sub/sync/info", "userList");
            return true;
        } else {
            return false;
        }
    }
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public boolean emailChecker(@RequestBody User user){
        log.info(user.getEmail());
        return userService.emailCheck(user.getEmail());
    }

    @RequestMapping(value = "/channel/{channel_id}")
    public List<User> getChannelUserList(@PathVariable int channel_id) {
        return userService.getCurrentChannelUserList(channel_id);
    }

}
