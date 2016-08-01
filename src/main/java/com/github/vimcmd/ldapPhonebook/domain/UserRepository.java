package com.github.vimcmd.ldapPhonebook.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserRepository {

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    Environment env;

    private static final String NORMAL_USER_ACCOUNT = "805306368";
    private static final String MACHINE_ACCOUNT = "805306369";
    private static final String ACCOUNTDISABLE_PASSWDNOTREQD_NORMALACCOUNT = "514";
    private static final String ACCOUNTDISABLE_NORMALACCOUNT = "546";
    private static final String PASSWDNOTREQD_NORMALACCOUNT_DONTEXPIREDPASSWD = "66080"; // exchange health mailboxes, etc.

    public List<List<User>> findAllGroupByDepartment() {
        return findAllGroupByDepartment("");
    }

    public List<List<User>> findAllGroupByDepartment(String samAccountName) {
        AndFilter andFilter = getAndFilter();
        if (StringUtils.hasText(samAccountName)) {
            andFilter.and(new EqualsFilter("samAccountName", samAccountName));
        }
        SearchControls searchControls = getSearchControls();

        final List<User> foundUsers = ldapTemplate.search("", andFilter.encode(), searchControls, new UserAttributesMapper());

        Map<String, List<User>> grouped = foundUsers.stream().collect(Collectors.groupingBy(User::getDepartment));
        List<List<User>> usersGroupedByDepartment = new ArrayList<>(grouped.values());

        Collections.sort(usersGroupedByDepartment, (o1, o2) -> (o1.get(0).getDepartment()).compareTo(o2.get(0).getDepartment()));

        for(List<User> group : usersGroupedByDepartment) {
            Collections.sort(group, (o1, o2) -> (o1.getTitle()).compareTo(o2.getTitle()));
        }

        return usersGroupedByDepartment;
    }

    private AndFilter getAndFilter() {

        // TODO: 08.07.2016 also filter by empty phone numbers

        AndFilter andFilter = new AndFilter();
        andFilter.and(new NotFilter(new OrFilter().or(new EqualsFilter("userAccountControl", ACCOUNTDISABLE_PASSWDNOTREQD_NORMALACCOUNT))
                                                  .or(new EqualsFilter("userAccountControl", ACCOUNTDISABLE_NORMALACCOUNT))
                                                  .or(new EqualsFilter("userAccountControl", PASSWDNOTREQD_NORMALACCOUNT_DONTEXPIREDPASSWD))));
        andFilter.and(new EqualsFilter("objectClass", "person"));
        andFilter.and(new EqualsFilter("samAccountType", NORMAL_USER_ACCOUNT));
        andFilter.and(new PresentFilter("mail"));
        return andFilter;
    }

    private SearchControls getSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // FIXME: 08.07.2016 figure out why max limit is 1000
        searchControls.setCountLimit(Long.parseLong(env.getProperty("ldap.searchControls.countLimit")));
        //String[] attrIDs = {"distinguishedName", "SamAccountName", "Name", "Department", "title", "telephoneNumber", "otherTelephone"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

    private static final class UserAttributesMapper implements AttributesMapper<User> {

        private static String getAttributeValue(@NotNull Attributes attributes, @NotNull String attrID) {
            // FIXME: 01.08.2016 optimize this
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
