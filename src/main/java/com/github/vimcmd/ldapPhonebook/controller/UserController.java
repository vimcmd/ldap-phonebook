package com.github.vimcmd.ldapPhonebook.controller;

import com.github.vimcmd.ldapPhonebook.domain.User;
import com.github.vimcmd.ldapPhonebook.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class UserController {

    @Autowired
    Environment env;

    @Autowired
    UserRepository userRepository;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public final String index(Model model, @RequestParam(required = false) String id) {

        List<List<User>> usersGroupedByDepartment = userRepository.findAllGroupByDepartment(id);

        int foundCountTotal = 0;
        for(List<User> usersGroup : usersGroupedByDepartment) {
            foundCountTotal += usersGroup.size();
        }
        logger.info("Users found: " + foundCountTotal);

        model.addAttribute("usersGroupedByDepartment", usersGroupedByDepartment);

        return "listUsers";
    }
}
