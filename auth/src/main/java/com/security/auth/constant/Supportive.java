package com.security.auth.constant;

public enum Supportive {

    ;

    public static class Roles {
        public static final String ADMIN = "ADMIN_ROLE";
        public static final String USER = "USER_ROLE";
    }

    public class SQLQuery {
        public static final String SQL_INSERT_USERS = "INSERT INTO users(login, password) VALUES (?, ?)";
        public static final String SQL_UPDATE_USERS = "UPDATE users SET login = ?, password = ? where login = ?";
        public static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users password = ? where id = ?";
        public static final String SQL_GET_USERS = "SELECT t1.login, t1.password, t3.role FROM users t1 JOIN authority_user t2 ON t1.id = t2.user_id JOIN roles t3 ON t2.role_id = t3.id WHERE login = ?";
        public static final String SQL_GET_ID_USER = "SELECT id FROM users WHERE login = ?";
        public static final String SQL_INSERT_AUTHORITY_BY_USER_ID = "INSERT INTO authority_user(user_id, role_id) VALUES (?, ?)";
        public static final String SQL_INSERT_USER_INFORMATION_BY_USER_ID = "INSERT INTO user_information(user_id, birthday, full_name, salary) VALUES (?, ?, ?, ?)";
        public static final String SQL_UPDATE_USER_INFORMATION_BY_USER_ID = "UPDATE INTO user_information(user_id, birthday, full_name, salary) VALUES (?, ?, ?, ?)";
        public static final String SQL_EXISTS_USER_BY_LOGIN = "SELECT EXISTS(SELECT users.login FROM users where login = ? limit 1)";
        public static final String SQL_GET_ROLE_BY_NAME = "SELECT id FROM roles where role = ?";
        public static final String SQL_DELETE_USER_BY_LOGIN = "DELETE FROM users WHERE login = ?";
    }

}
