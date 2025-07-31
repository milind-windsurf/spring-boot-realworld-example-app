create table users (
  id varchar(255) primary key,
  username varchar(255) UNIQUE,
  password varchar(255),
  email varchar(255) UNIQUE,
  bio text,
  image varchar(511)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table articles (
  id varchar(255) primary key,
  user_id varchar(255),
  slug varchar(255) UNIQUE,
  title varchar(255),
  description text,
  body text,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table article_favorites (
  article_id varchar(255) not null,
  user_id varchar(255) not null,
  primary key(article_id, user_id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table follows (
  user_id varchar(255) not null,
  follow_id varchar(255) not null
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table tags (
  id varchar(255) primary key,
  name varchar(255) not null
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table article_tags (
  article_id varchar(255) not null,
  tag_id varchar(255) not null
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

create table comments (
  id varchar(255) primary key,
  body text,
  article_id varchar(255),
  user_id varchar(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
