package com.github.vimcmd.ldapPhonebook;

import com.github.vimcmd.ldapPhonebook.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class Application {

    private Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    LdapTemplate ldapTemplate;

    @Bean
    @ConfigurationProperties(prefix = "ldap.contextSource")
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        //contextSource.setReferral("follow");
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }

    //@Bean
    //public UserRepository userRepository(LdapTemplate ldapTemplate) {
    //    return new UserRepository() {
    //        @Override
    //        public List<User> findByFullNameContains(String name) {
    //            return null;
    //        }
    //
    //        @Override
    //        public User findOne(LdapQuery ldapQuery) {
    //            return null;
    //        }
    //
    //        @Override
    //        public Iterable<User> findAll(LdapQuery ldapQuery) {
    //            return ldapTemplate.findAll(User.class);
    //        }
    //    };
    //}

}
