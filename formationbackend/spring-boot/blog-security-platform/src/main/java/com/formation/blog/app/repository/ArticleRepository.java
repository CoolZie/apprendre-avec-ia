package com.formation.blog.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formation.blog.app.model.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
