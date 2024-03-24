package com.example.project.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 返回结果（分页）
 * @param <R>
 */
@Data
public class Paging<R> implements Serializable {

    public static int DEFAULT_PAGE_SIZE = 20;

    //页数
    private int pageNum;

    //每页数量
    private int pageSize = 20;

    //总页数
    private int totalPage;

    //总记录数
    private long totalCount;

    //数据
    private List<R> data;

    public Paging() {

    }

    public Paging(int pageNum, int pageSize, int totalPage, long totalCount, List<R> data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.data = data;
    }
}
