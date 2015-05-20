package com.github.tkurz.lido.core;

import java.util.List;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public class Page<T> {

    int page;
    int numberOfPages;
    int pagesSize;

    private List<T> content;

    public boolean hasNext() {
        return page < numberOfPages;
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(1,page);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getPagesSize() {
        return pagesSize;
    }

    public void setPagesSize(int pagesSize) {
        this.pagesSize = pagesSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
