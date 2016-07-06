package com.github.vimcmd.ldapPhonebook.controller;

import com.github.vimcmd.ldapPhonebook.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    LdapTemplate ldapTemplate;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String index(Model model, @RequestParam(required = false) String name) {
        if (StringUtils.hasText(name)) {
            model.addAttribute("users", "NOT YET IMPLEMENTED");
        } else {
            final List<User> allUsers = ldapTemplate.findAll(User.class);
            logger.warn(allUsers.toString());
            model.addAttribute("users", allUsers);
        }
        return "listUsers";
    }

}
