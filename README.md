
db is not (trivially) interchangable. It's prepared/hardcoded for postgres with possibility to use H2

set up your etc/hosts:
```
127.0.0.1  fofobar.com
127.0.0.1  auth.fofobar.com
```

- interface is only shared stuff required to be able to do auths

- introduces auth db under @NamedDatabase("auth")

- need to enable modules from auth that you want to use