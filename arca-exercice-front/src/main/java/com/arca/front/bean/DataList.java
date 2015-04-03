package com.arca.front.bean;

import com.arca.front.bean.Data;

import java.util.Collection;
import java.util.List;

/**
 * Define data structure return to client
 */
public class DataList {

    private Collection<Data> dataEntities;
    private int page;
    private int count;
    private int pages;
    private int size;
    private String sortBy;
    private String sortOrder;

    public Collection<Data> getDataEntities() {
        return dataEntities;
    }

    public void setDataEntities(Collection<Data> dataEntities) {
        this.dataEntities = dataEntities;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
