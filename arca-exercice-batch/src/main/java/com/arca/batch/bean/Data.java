package com.arca.batch.bean;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document()
public class Data {
    // id is auto-generated

    private Date date;
    private Integer value;
    private String country;
    private Long line;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return this.getDate() + ", " + this.getValue() + ", " + this.getCountry();
    }
}
