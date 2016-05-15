## Auth api

Projects are split into modules. If in future we wish to deploy separatedly (microservices and such),
they should be split by those.

For authentication your API can either depend on `auth-direct` project, to hit directly DB, or use
`auth-http` for cleaner authentication via HTTP (your api does http request behind the scenes on auth-api,
so it must be running at this point).

There are lots of TODOs, but hopefully nothing massive.

For tests you need to run docker as mentioned below.

## Layering

Code is simply layered to keep dependencies linear.
`Model <- Persistence <- Service <- Formatting <- Controllers`
i.e., Service can depend on interfaces from Persistence and Model.

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

