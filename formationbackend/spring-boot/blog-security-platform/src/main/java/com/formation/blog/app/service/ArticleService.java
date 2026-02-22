package com.formation.blog.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.formation.blog.app.dto.ArticleRequest;
import com.formation.blog.app.dto.ArticleResponse;
import com.formation.blog.app.exception.ResourceNotFoundException;
import com.formation.blog.app.model.Article;
import com.formation.blog.app.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleResponse> findAll() {
        return articleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ArticleResponse findById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));
        return toResponse(article);
    }

    public ArticleResponse create(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setPublished(request.isPublished());
        return toResponse(articleRepository.save(article));
    }

    public ArticleResponse update(Long id, ArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setPublished(request.isPublished());
        return toResponse(articleRepository.save(article));
    }

    public ArticleResponse publish(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));
        article.setPublished(true);
        return toResponse(articleRepository.save(article));
    }

    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found: " + id);
        }
        articleRepository.deleteById(id);
    }

    private ArticleResponse toResponse(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.isPublished(),
                article.getCreatedAt(),
                article.getUpdatedAt());
    }
}
