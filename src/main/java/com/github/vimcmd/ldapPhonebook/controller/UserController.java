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
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    Environment env;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public final String index(Model model, @RequestParam(required = false) String uid) {

        AndFilter andFilter = getAndFilter();
        if (StringUtils.hasText(uid)) {
            andFilter.and(new EqualsFilter("samAccountName", uid));
        }

        SearchControls searchControls = getSearchControls();

        final List<User> foundUsers = ldapTemplate.search("", andFilter.encode(), searchControls, new UserAttributesMapper());
        foundUsers.sort((o1, o2) -> o1.getSamAccountName().compareTo(o2.getSamAccountName()));

        model.addAttribute("foundUsers", foundUsers);
        model.addAttribute("foundCount", foundUsers.size());
        fillEnvironmentAttributes(model);

        //logger.info("USERS: " + foundUsers.toString());
        return "listUsers";
    }

    private AndFilter getAndFilter() {
        final String NORMAL_USER_ACCOUNT = "805306368";
        final String MACHINE_ACCOUNT = "805306369";

        AndFilter andFilter = new AndFilter();
        andFilter.and(new LikeFilter("objectClass", "person"));
        andFilter.and(new LikeFilter("samAccountType", NORMAL_USER_ACCOUNT));
        return andFilter;
    }

    private SearchControls getSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setCountLimit(Long.parseLong(env.getProperty("ldap.searchControls.countLimit")));
        //String[] attrIDs = {"distinguishedName", "SamAccountName", "Name", "Department", "title", "telephoneNumber", "otherTelephone"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

    private void fillEnvironmentAttributes(Model model) {
        List<String> environmentProperties = new ArrayList<>();
        environmentProperties.add(env.getProperty("ldap.contextSource.url"));
        environmentProperties.add(env.getProperty("ldap.contextSource.userDn"));
        model.addAttribute("envValues", environmentProperties);
        //logger.info("VALUES: " + environmentProperties);
    }

    private static final class UserAttributesMapper implements AttributesMapper<User> {

        private static String getAttributeValue(@NotNull Attributes attributes, @NotNull String attrID) {
            final Attribute attribute = attributes.get(attrID);
            if (attribute != null) {
                return String.valueOf(attribute).split(": ", 2)[1];
            }
            return "";
        }

        @Override
        public User mapFromAttributes(Attributes attributes) throws NamingException {
            User user = new User();
            user.setSamAccountName(getAttributeValue(attributes, "samAccountName"));
            user.setFullName(getAttributeValue(attributes, "cn"));
            user.setMail(getAttributeValue(attributes, "mail"));
            user.setDepartment(getAttributeValue(attributes, "department"));
            user.setTitle(getAttributeValue(attributes, "title"));
            user.setTelephoneNumber(getAttributeValue(attributes, "telephoneNumber"));
            user.setOtherTelephone(getAttributeValue(attributes, "otherTelephone"));
            return user;
        }
    }

}
