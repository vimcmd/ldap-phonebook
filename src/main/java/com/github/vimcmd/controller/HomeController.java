package com.github.vimcmd.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String providePhoneBook(Model model) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.warn("AUTHENTICATED: " + authentication.getPrincipal().toString());

        model.addAttribute("currentUser", authentication.getName());
        return "phonebook";
    }

}
