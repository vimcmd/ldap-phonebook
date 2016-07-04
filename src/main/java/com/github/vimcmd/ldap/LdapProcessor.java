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

    private static final ResourceBundle connection = ResourceBundle.getBundle("connection");
    private static final String PRINCIPAL = connection.getString("ldap.security.principal");
    private static final String CREDENTIALS = connection.getString("ldap.security.credentials"); // TODO: 04.07.2016 make char array
    private static final String PROVIDER_URL = connection.getString("ldap.provider.url");
    private static final String CONTEXT_SEARCH = connection.getString("ldap.context.search");

    public static void main(String[] args) {
        System.out.println("run: " + new Date());
        LdapContext ldapContext = getLdapContext();
        SearchControls searchControls = getSearchControls();
        getUserInfo("44kvp", ldapContext, searchControls);
        getUserInfo("44aai", ldapContext, searchControls);
        getUserInfo("fake_user", ldapContext, searchControls);
        System.out.println("done: " + new Date());
    }

    private static LdapContext getLdapContext() {
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

    private static User getUserInfo(String userName, LdapContext ctx, SearchControls searchControls) {
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

    private static SearchControls getSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = {"distinguishedName", "SamAccountName", "Name", "Department", "title", "telephoneNumber", "otherTelephone"};
        cons.setReturningAttributes(attrIDs);
        return cons;
    }

}
