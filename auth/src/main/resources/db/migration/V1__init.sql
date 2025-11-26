CREATE TABLE IF NOT EXISTS users(
    id bigint GENERATED ALWAYS AS IDENTITY,
    login varchar(50) not null unique,
    password varchar(80) not null,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS roles(
    id int GENERATED ALWAYS AS IDENTITY,
    name varchar(50) not null unique,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS authority_user(
    id bigint GENERATED ALWAYS AS IDENTITY,
    role_id int not null,
    user_id bigint not null,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_information(
    id bigint not null,
    full_name varchar(80),
    birthday timestamp without time zone,
    salary double precision,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);
