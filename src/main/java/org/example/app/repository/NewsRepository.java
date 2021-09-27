package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.News;
import org.example.app.domain.User;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class NewsRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<News> rowMapper = resultSet -> new News(
            resultSet.getLong("id"),
            resultSet.getString("title"),
            resultSet.getString("text"),
            resultSet.getTimestamp("created")
    );
    public Optional<News> saveNews(String title, String text) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                    INSERT INTO news(title, text) VALUES (?, ?) 
                    RETURNING id, title,text, created
                    """,
                rowMapper,
                title, text
        );
    }
    public List<News> getAll() {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                "SELECT * FROM news ",
                rowMapper);
    }
}
