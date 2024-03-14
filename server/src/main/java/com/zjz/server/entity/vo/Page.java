package com.zjz.server.entity.vo;

import lombok.Data;

/**
 * 封装分页相关的信息
 */
@Data
public class Page {

    //当前页码
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 数据总数（用于计算总页数）
    private int rows;

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        return rows / limit + (rows % limit == 0 ? 0 : 1);
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        return Math.max(current - 2, 1);
    }

    /**
     * 获取结束页
     * @return
     */
    public int getTo() {
        int total = getTotal();
        return Math.min(current + 2, total);
    }
}
