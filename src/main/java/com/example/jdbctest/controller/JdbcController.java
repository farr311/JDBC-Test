package com.example.jdbctest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

@RestController
public class JdbcController {

    @Autowired
    JdbcTemplate template;

    @GetMapping("jdbc")
    public void perform() {
        template.execute(
                "CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));;");

        PreparedStatementCreator creator= con -> con.prepareStatement("INSERT INTO TEST VALUES(?, ?)");

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            PreparedStatementCallback<Boolean> callback = statement -> {
                statement.setInt(1, finalI + 1);
                statement.setString(2, "test_" + finalI);
                return statement.execute();
            };

            template.execute(creator, callback);
        }

        Stream<User> s = template.queryForStream(
                "SELECT * FROM TEST;",
                (rs, rowNum) -> {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");

                    return new User(id, name);
                }
        );

        s.forEach(System.out::println);
    }
}

class User {
    private final int id;
    private final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
