This is example on having central REST endpoint for auth related stuff, and securing other endpoints
using same known silhouette mechanism. Currently each client hits DB containing authentication data
on its own, but that can be implemented differently if one wishes to have single central point.

# Architecture

`Auth-core` contains shared functionality that APIs that require authentication and authorization
should depend on. It introduces configurable stuff which can be found in reference.conf. You need
to enable some default functionality in modules (check auth-api's application.conf)

`Auth-api` is api for auth related stuff. You can do stuff as signing up/in, giving permissions etc.

`Some-auth-core` represents some unrelated API that would like to use same authentication, in other words,
someone can register on Auth-api and can then use same login on different api.

## Layering

Code is simply layered to keep dependencies linear.
`Model <- Persistence <- Service <- Formatting <- Controllers`
i.e., Service can depend on interfaces from Persistence and Model.

## Separation

DAOs - data access objects - execute queries against database.

Repos - repositories - return actions, and it's up to caller to execute them, thus giving more fine-grained
control over execution (if you want to run them in transactions etc), usually in services.

Services - encapsulate business logic decision, and usually their code hit database (directly or through DAOs)

# Try it out

First, set up your `etc/hosts`:
```
127.0.0.1 fofobar.com
```

Spin up nginx using configuration:

    location / {
      root   /Users/?someuser?/Code/play-slick-silhouette-auth-api/modules/webs/;
      index  index.html index.htm;
    }

    location /auth/ {
      proxy_pass http://127.0.0.1:9000/;
    }

    location /client/ {
      proxy_pass http://127.0.0.1:9001/;
    }

Start the Postgres database server:

    docker run -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 postgres

Start auth api:

    sbt ";project authApi; run 9000"
    
Start Play clients:

    sbt ";project someAuthClient; run 9001"

Access websites through [http://fofobar.com/web1/](http://fofobar.com/web1/), [http://fofobar.com/web2/](http://fofobar.com/web2/) and [http://fofobar.com/web-ext/](http://fofobar.com/web-ext/). Read messages in the console.

### TODO
- Rest API Silhoette client (so clients don't need to hit DB and get all bunch of dependencies...)
- Other providers
