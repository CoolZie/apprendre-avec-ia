package com.exercice1.demo.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Wrapper générique pour les réponses paginées")
public class PagedResponse<T> {
    
    @Schema(description = "Liste des éléments de la page courante")
    private List<T> content;
    
    @Schema(description = "Numéro de la page courante (commence à 0)", example = "0")
    private int pageNumber;
    
    @Schema(description = "Nombre d'éléments par page", example = "10")
    private int pageSize;
    
    @Schema(description = "Nombre total d'éléments", example = "150")
    private long totalElements;
    
    @Schema(description = "Nombre total de pages", example = "15")
    private int totalPages;
    
    @Schema(description = "True si c'est la première page", example = "true")
    private boolean isFirst;
    
    @Schema(description = "True si c'est la dernière page", example = "false")
    private boolean isLast;
    
    @Schema(description = "True s'il existe une page suivante", example = "true")
    private boolean hasNext;
    
    @Schema(description = "True s'il existe une page précédente", example = "false")
    private boolean hasPrevious;
    
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}
