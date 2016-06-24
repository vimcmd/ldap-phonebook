package com.github.vimcmd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String providePhoneBook(Model model) {
        // TODO: 24.06.2016 replace Object with phonebook domain object
        model.addAttribute("users", new Object());
        return "phonebook";
    }

}
