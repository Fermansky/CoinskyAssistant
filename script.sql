create table images
(
    item_id int          not null,
    img_url varchar(100) null
);

create table items
(
    id            int          not null,
    name          varchar(100) null,
    description   text         null,
    price         int          null,
    url           varchar(100) null,
    create_time   datetime     null,
    thumbnail_url varchar(100) null
);


