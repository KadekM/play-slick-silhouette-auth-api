curl -X POST http://localhost:9000/signup -H 'Content-Type: application/json' -d '{"firstName": "Marek", "lastName": "Something", "identifier": "marek@foo.bar",  "password": "somestrongpassword123!"}' -v