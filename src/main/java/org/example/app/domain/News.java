package org.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class News {
    private long id;
    private String title;
    private String text;
    private Timestamp created;
}
