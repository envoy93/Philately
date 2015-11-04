package com.philately.model;

import javax.persistence.*;

/**
 * Created by kirill on 25.10.2015.
 */

@Entity
@Table(name = "Stock")
public class Stock implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stockId;
    private String stockCode;
    private String stockName;

    public Stock() {
    }

    public Stock(String stockCode, String stockName) {
        this.stockCode = stockCode;
        this.stockName = stockName;
    }

    public Integer getStockId() {
        return this.stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getStockCode() {
        return this.stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return this.stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

}
