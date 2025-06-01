# Netty Game Server

This module contains a simple TCP game server built with Netty and MySQL.
It demonstrates a basic username/password login over a raw TCP connection.

## Build

Use Maven to build the project:

```bash
cd netty-game-server
mvn package
```

The build produces `target/netty-game-server-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Run

Edit `UserDao` if you need to change the MySQL connection parameters.

Run the server (default port `8080`):

```bash
java -jar target/netty-game-server-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Protocol

Clients send a single line command:

```
LOGIN <username> <password>\n
```

The server replies with either `LOGIN SUCCESS` or `LOGIN FAILED` followed by a newline.
