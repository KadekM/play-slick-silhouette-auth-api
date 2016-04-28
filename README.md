
db is not (trivially) interchangable. It's prepared/hardcoded for postgres with possibility to use H2

set up your etc/hosts:
```
127.0.0.1 fofobar.com
```

nginx config:
```
  location / {
            root   /Users/marekkadek/Code/play-slick-silhouette-auth-api/modules/webs/;
            index  index.html index.htm;
        }

  location /auth/ {
	    proxy_pass http://127.0.0.1:9000/;
    }

  location /client/ {
	    proxy_pass http://127.0.0.1:9001/;
	}
```

Start play clients AUTH via `run 9000` and CLIENT `run 9001`

Access websites through http://fofobar.com/web1/ and http://fofobar.com/web2/ and http://fofobar.com/web-ext/.


- interface is only shared stuff required to be able to do auths

- introduces auth db under @NamedDatabase("auth")

- need to enable modules from auth that you want to use

- document permissions

- document difference between dakos etc