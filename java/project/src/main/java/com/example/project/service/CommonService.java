package com.example.project.service;

import com.example.project.common.Paging;

import java.util.List;

public interface CommonService {

    Paging info(String keyword, List<String> genres, List<String> styles, List<String> formats, List<String> forms, List<String> countries, List<String> decades, int currentPage, int pageSize, int order, String area);
}
