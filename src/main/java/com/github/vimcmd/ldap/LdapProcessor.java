package com.github.vimcmd.ldap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.registry.infomodel.User;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class LdapProcessor {

    private final ResourceBundle connection = ResourceBundle.getBundle("ldap-connection");
    private final String PRINCIPAL = connection.getString("ldap.userDn");
    private final String CREDENTIALS = connection.getString("ldap.password"); // TODO: 04.07.2016 make char array
    private final String PROVIDER_URL = connection.getString("ldap.url");
    private final String CONTEXT_SEARCH = connection.getString("ldap.base");

    public static void main(String[] args) {
        LdapProcessor ldapProcessor = new LdapProcessor();
        System.out.println("run: " + new Date());
        LdapContext ldapContext = ldapProcessor.getLdapContext();
        SearchControls searchControls = ldapProcessor.getSearchControls();

        ldapProcessor.getUserInfo("44kvp", ldapContext, searchControls);
        ldapProcessor.getUserInfo("44aai", ldapContext, searchControls);
        ldapProcessor.getUserInfo("fake_user", ldapContext, searchControls);

        System.out.println("done: " + new Date());
    }

    private LdapContext getLdapContext() {
        LdapContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");

            env.put(Context.SECURITY_PRINCIPAL, PRINCIPAL);//input user & password for access to ldap
            env.put(Context.SECURITY_CREDENTIALS, CREDENTIALS);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            env.put(Context.REFERRAL, "follow");
            ctx = new InitialLdapContext(env, null);
            System.out.println("LDAP Connection: COMPLETE");
        } catch (NamingException nex) {
            System.out.println("LDAP Connection: FAILED");
            nex.printStackTrace();
        }
        return ctx;
    }

    private User getUserInfo(String userName, LdapContext ctx, SearchControls searchControls) {
        System.out.println("\n*** " + userName + " ***");
        User user = null;
        try {
            NamingEnumeration<SearchResult> answer = ctx.search(CONTEXT_SEARCH, "samAccountName=" + userName, searchControls);
            if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                System.out.println(attrs.get("distinguishedName"));
                System.out.println(attrs.get("SamAccountName"));
                System.out.println(attrs.get("Name"));
                System.out.println(attrs.get("Department"));
                System.out.println(attrs.get("title"));
                System.out.println(attrs.get("telephoneNumber"));
                System.out.println(attrs.get("otherTelephone"));
            } else {
                System.out.println("user not found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    private SearchControls getSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {"distinguishedName", "SamAccountName", "Name", "Department", "title", "telephoneNumber", "otherTelephone"};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }

}
