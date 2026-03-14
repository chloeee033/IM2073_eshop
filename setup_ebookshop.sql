create database if not exists ebookshop;

use ebookshop;

drop table if exists books;
create table books (
  id     int,
  title  varchar(50),
  author varchar(50),
  price  float,
  qty    int,
  primary key (id)
);

insert into books values (1001, 'Java for dummies', 'Tan Ah Teck', 11.11, 11);
insert into books values (1002, 'More Java for dummies', 'Tan Ah Teck', 22.22, 22);
insert into books values (1003, 'More Java for more dummies', 'Mohammad Ali', 33.33, 33);
insert into books values (1004, 'A Cup of Java', 'Kumar', 44.44, 44);
insert into books values (1005, 'A Teaspoon of Java', 'Kevin Jones', 55.55, 55);
insert into books values (1006, 'Java Basics in Practice', 'Tan Ah Teck', 18.88, 18);
insert into books values (1007, 'Advanced Java Workshop', 'Tan Ah Teck', 29.99, 14);
insert into books values (1008, 'Servlets Made Simple', 'Tan Ah Teck', 24.50, 20);
insert into books values (1009, 'Learning JDBC', 'Tan Ah Teck', 26.75, 16);
insert into books values (1010, 'Web Apps with Java', 'Mohammad Ali', 31.20, 19);
insert into books values (1011, 'Database Design for Beginners', 'Mohammad Ali', 27.40, 17);
insert into books values (1012, 'Mastering SQL Queries', 'Mohammad Ali', 35.60, 21);
insert into books values (1013, 'Java Patterns Explained', 'Mohammad Ali', 39.90, 12);
insert into books values (1014, 'Tomcat Essentials', 'Kumar', 28.80, 15);
insert into books values (1015, 'Building Online Bookstores', 'Kumar', 42.00, 13);
insert into books values (1016, 'HTML Forms and Servlets', 'Kumar', 23.95, 24);
insert into books values (1017, 'Practical Web Databases', 'Kevin Jones', 37.25, 11);
insert into books values (1018, 'Java Project Recipes', 'Kevin Jones', 32.10, 10);

select * from books;
