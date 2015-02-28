package com.smalser.pdat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class InMemoryDBInitializer
{
    private final JdbcTemplate template;

    @Autowired
    public InMemoryDBInitializer(DataSource dataSource)
    {
        template = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init()
    {
        template.execute("CREATE  TABLE users (\n" +
                "  username VARCHAR(45) NOT NULL ,\n" +
                "  password VARCHAR(45) NOT NULL ,\n" +
                "  enabled BOOLEAN NOT NULL DEFAULT TRUE,\n" +
                "  PRIMARY KEY (username))");

        template.execute("CREATE TABLE user_roles (\n" +
                "  user_role_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n" +
                "  username VARCHAR(45) NOT NULL,\n" +
                "  ROLE VARCHAR(45) NOT NULL,\n" +
                "  CONSTRAINT USER_ROLE_ID_PK PRIMARY KEY (user_role_id),\n" +
                "  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username))");

        template.execute("ALTER TABLE user_roles ADD CONSTRAINT uni_username_role UNIQUE (ROLE, username)");

        template.batchUpdate(new String[]{
                "INSERT INTO users(username, password, enabled) VALUES ('smal', 'smal', TRUE)",
                "INSERT INTO users(username, password, enabled) VALUES ('alex', 'alex', TRUE)",
                "INSERT INTO user_roles(username, ROLE) VALUES ('smal', 'ROLE_USER')",
                "INSERT INTO user_roles(username, ROLE) VALUES ('smal', 'ROLE_ADMIN')",
                "INSERT INTO user_roles(username, ROLE) VALUES ('alex', 'ROLE_USER')",

        });

        System.out.println("InMemoryDBInitializer.init");
    }
}
