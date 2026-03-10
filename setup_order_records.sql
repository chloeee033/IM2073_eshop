use ebookshop;

drop table if exists order_records;
create table order_records (
  id          int,
  qty_ordered int,
  cust_name   varchar(30),
  cust_email  varchar(30),
  cust_phone  char(8)
);
