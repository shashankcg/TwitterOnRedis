# TwitterOnRedis
# Twitter like service with Spring Security(including LDAP), Spring Boot, REDIS Caching 

## Prerequisites
- JDK 1.8 or later
- Maven 3 or later
- Redis 3.2.11 started with protected mode off

## Stack
- Spring Security
- Spring Boot
- Maven
- REDIS
- LDAP Server

## Run
Installing REDIS:
https://redis.io/topics/quickstart

wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
make test
cd src
./redis-server --protected-mode no & (This starts the service on port 8379)

Run the service:
```mvn clean spring-boot:run```

## Description

This is a twitter like service built on REDIS cache for providing high performance results 

User management is done through LDAP.
An LDAP server is embedded into the service.
Spring Boot is used to build the application.

REDIS cache is used as the store for the following use cases:

1. Storing all tweets in the global tweet index
2. Adding tweetID to the user timeline index for each user
3. Mapping users their followers in the user_followers index


Embedded AD is used as the source for authentication
Sample user/passwords: bob/ben/joe = bobspassword/benspassword/joespassword
Global dummy user: user1/12345




