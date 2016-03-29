# Docker hosts schema

# !--- !Ups

CREATE TABLE hosts (
  url varchar(255) NOT NULL PRIMARY KEY,
  status varchar(255)
);

INSERT INTO hosts VALUES("http://localhost:4243", "disconnected");

# !-- !Downs

drop table hosts;