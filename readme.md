# LDAP-based phone book

Tested on Active Directory

## Configure application

Main configuration file `application.properties` must contain variables (without `{}`):

```
ldap.contextSource.url = {ldap://url:389}
ldap.contextSource.userDn = {user distinguished name, for ex: CN=Karen Berge,CN=user,DC=corp,DC=Fabrikam,DC=COM}
ldap.contextSource.password = {user password}
ldap.contextSource.base = {search base (for now including subrees), for ex: DC=corp,DC=Fabrikam,DC=COM}
ldap.searchControls.countLimit = {search count limit}
```

## REST API

#### GET phone book

This endpoint returns HTML table containing departments, users and their job titles, mailboxes and phones.

**HTTP REQUEST**

```
 http://localhost:8080/users
```
