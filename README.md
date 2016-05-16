## Overview

Projects are split into modules; this would support fine-grained deployment, for example microservices. Two module 'packages' are used: `auth` and `bar`.

**auth**


 * `auth-core` - Provides basic functionality used by all other modules, including `auth-direct`.
 * `auth-direct` - Provides the simplest and most direct implementation of an authentication/authorization API based on Silhouette and Play Framework. `auth-direct` contains the model, DAOs and related queries.
 * `auth-api` - builds on `auth-direct` to provide endpoints for user sign up, token validation, sending emails (soon), and more to come.
 * `auth-http` - provides an authentication service over HTTP by leveraging `auth-api`.

**bar**

 * bar-api - example external API

## Layering

Code is layered:

`Model <- Persistence <- Service <- Formatting <- Controllers`

For example, `Service` can depend on interfaces from `Persistence` and `Model`.

## Separation

DAOs - data access objects - execute queries against database.

Services - encapsulate business logic decision, and usually their code hit database (directly or through DAOs)

## Tests

If you wish to run local databases (which you should), you can use docker to do that easily:
```
➜ docker run --name auth-db-dev -d -e POSTGRES_PASSWORD=mysecretpassword -p 9050:5432 postgres
➜ docker run --name auth-db-test -d -e POSTGRES_PASSWORD=mysecretpassword -p 9090:5432 postgres
```

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
