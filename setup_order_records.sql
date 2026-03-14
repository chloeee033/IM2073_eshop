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
