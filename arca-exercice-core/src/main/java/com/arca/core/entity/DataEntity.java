package com.arca.core.entity;

import org.mongodb.morphia.annotations.Entity;

import java.util.Date;

/**
 * Data com.arca.core.entity
 */
@Entity("data")
public class DataEntity {
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
        return "Line " + this.getLine() + " : " + this.getDate() + ", " + this.getValue() + ", " + this.getCountry();
    }
}
