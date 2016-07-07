package com.github.vimcmd.ldapPhonebook.controller;

import com.github.vimcmd.ldapPhonebook.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    Environment env;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String index(Model model, @RequestParam(required = false) String uid) {

        AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter("objectclass","person"));
        if (StringUtils.hasText(uid)) {
            andFilter.and(new EqualsFilter("samAccountName", uid));
        }
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        final List<User> foundUsers = ldapTemplate.search(
                "",
                andFilter.encode(),
                searchControls,
                (AttributesMapper<User>) attributes -> {
                    User user = new User();
                    user.setSamAccountName(String.valueOf(attributes.get("samAccountName")));
                    user.setFullName(String.valueOf(attributes.get("cn")));
                    user.setDepartment(String.valueOf(attributes.get("department")));
                    user.setTitle(String.valueOf(attributes.get("title")));
                    user.setTelephoneNumber(String.valueOf(attributes.get("telephoneNumber")));
                    user.setOtherTelephone(String.valueOf(attributes.get("otherTelephone")));
                    return user;
                }
        );

        model.addAttribute("foundUsers", foundUsers);
        fillEnvironmentAttributes(model);
        //logger.info("USERS: " + foundUsers.toString());
        return "listUsers";
    }

    private void fillEnvironmentAttributes(Model model) {
        List<String> environmentProperties = new ArrayList<>();
        environmentProperties.add(env.getProperty("ldap.contextSource.url"));
        environmentProperties.add(env.getProperty("ldap.contextSource.userDn"));
        model.addAttribute("envValues", environmentProperties);
        //logger.info("VALUES: " + environmentProperties);
    }

}
