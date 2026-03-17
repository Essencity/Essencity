package com.xiaohongshu;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// 移除@SpringBootApplication注解，避免与主应用类冲突
public class DbConnectionTest {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DbConnectionTest.class, args);
        
        // 获取数据源
        DataSource dataSource = context.getBean(DataSource.class);
        
        // 测试数据库连接
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                System.out.println("✅ 数据库连接成功!");
                System.out.println("数据库 URL: " + connection.getMetaData().getURL());
                System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
                
                // 使用 JdbcTemplate 执行一个简单查询
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                String sql = "SELECT COUNT(*) FROM users";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
                System.out.println("用户表记录数: " + count);
                
                // 执行查询 posts 表
                sql = "SELECT COUNT(*) FROM posts";
                count = jdbcTemplate.queryForObject(sql, Integer.class);
                System.out.println("帖子表记录数: " + count);
                
            } else {
                System.out.println("❌ 数据库连接失败!");
            }
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭 Spring Boot 应用
            SpringApplication.exit(context);
        }
    }
}