package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.News;
import org.example.app.dto.NewsRequestDto;
import org.example.app.repository.NewsRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> getAllNews() {
        return repository.getAll();
    }

    public Optional<News> addNews(NewsRequestDto requestDto) {
        return repository.saveNews(requestDto.getTitle(), requestDto.getText());
    }
}
