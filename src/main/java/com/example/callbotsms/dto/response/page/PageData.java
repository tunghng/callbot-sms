package com.example.callbotsms.dto.response.page;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageData<T> {
    final private List<T> data;
    final private Integer totalPages;
    final private Long totalElements;
    final private Boolean hasNext;

    public PageData(Page<T> page) {
        this.data = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
    }
}
