package com.formation.blog.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 180, message = "Title must contain between 5 and 180 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 20, max = 5000, message = "Content must contain between 20 and 5000 characters")
    private String content;

    private boolean published;
}
