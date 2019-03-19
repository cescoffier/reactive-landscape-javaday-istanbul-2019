# The Reactive Landscape

Slides and demo presented at JavaDay Istanbul 2019
 
## Demo

1. Import the maven project in your IDE.
2. Open the `PricerService` class and run it (it contains a `main` method)
3. Launch postgresql using:

```bash
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 \
    --name postgres-quarkus-rest-http-crud -e POSTGRES_USER=restcrud \
    -e POSTGRES_PASSWORD=restcrud -e POSTGRES_DB=rest-crud \
    -p 5432:5432 postgres:10.5
```

5. Open the `App104` class and run it (it contains a `main` method)
6. Open a browser to http://localhost:8080/assets/index.html
 
To add an item with HTTPie:
 
```bash
$ echo "Bacon" | http :8080/products
```

## Quarkus

Compile with:

1. `mvn package -Pquarkus`
2. Build the Docker container with

```bash
docker build -t quarkus/my-quarkus-project .
```

3. Run it with

```bash
docker run -i --rm -p 8080:8080 quarkus/my-quarkus-project
```


IMPORTANT: Launch the PostgreSQL and Pricer service first.
 
 
