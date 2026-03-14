use ebookshop;

drop table if exists order_records;
drop table if exists order_items;
drop table if exists orders;
drop table if exists customers;

create table customers (
  customer_id  int auto_increment,
  cust_name    varchar(100) not null,
  cust_email   varchar(100) not null,
  cust_phone   varchar(20) not null,
  primary key (customer_id),
  unique key uk_customers_email (cust_email)
);

create table orders (
  order_id     int auto_increment,
  customer_id  int not null,
  order_time   datetime not null default current_timestamp,
  status       varchar(20) not null default 'PLACED',
  primary key (order_id),
  constraint fk_orders_customer
    foreign key (customer_id) references customers(customer_id)
);

create table order_items (
  order_item_id   int auto_increment,
  order_id        int not null,
  book_id         int not null,
  qty_ordered     int not null,
  price_at_order  decimal(10,2) not null,
  primary key (order_item_id),
  constraint fk_order_items_order
    foreign key (order_id) references orders(order_id),
  constraint fk_order_items_book
    foreign key (book_id) references books(id)
);

insert into customers (customer_id, cust_name, cust_email, cust_phone) values
  (1, 'Jesse Li', 'jesse@example.com', '81234567'),
  (2, 'Alice Tan', 'alice@example.com', '82345678'),
  (3, 'Brandon Lim', 'brandon@example.com', '83456789'),
  (4, 'Cheryl Ng', 'cheryl@example.com', '84567890'),
  (5, 'Daniel Koh', 'daniel@example.com', '85678901');

insert into orders (order_id, customer_id, order_time, status) values
  (1, 1, '2026-03-14 10:00:00', 'PLACED'),
  (2, 2, '2026-03-14 10:15:00', 'PLACED'),
  (3, 3, '2026-03-14 10:30:00', 'PLACED'),
  (4, 4, '2026-03-14 10:45:00', 'PLACED'),
  (5, 5, '2026-03-14 11:00:00', 'PLACED');

insert into order_items (order_item_id, order_id, book_id, qty_ordered, price_at_order) values
  (1, 1, 1004, 1, 44.44),
  (2, 1, 1014, 1, 28.80),
  (3, 2, 1010, 2, 31.20),
  (4, 2, 1001, 1, 11.11),
  (5, 3, 1017, 2, 37.25),
  (6, 4, 1007, 1, 29.99),
  (7, 4, 1014, 2, 28.80),
  (8, 5, 1001, 3, 11.11),
  (9, 5, 1010, 1, 31.20);

update books set qty = qty - 4 where id = 1001;
update books set qty = qty - 1 where id = 1004;
update books set qty = qty - 1 where id = 1007;
update books set qty = qty - 3 where id = 1010;
update books set qty = qty - 3 where id = 1014;
update books set qty = qty - 2 where id = 1017;
