DROP DATABASE IF EXISTS test;
CREATE DATABASE IF NOT EXISTS test;
CREATE USER IF NOT EXISTS 'test'@'%' IDENTIFIED BY 'test';
GRANT ALL PRIVILEGES ON *.* TO 'test'@'%';
