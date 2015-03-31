package com.arca.batch.bean;

public class DataTxt {
    private String timestamp;
    private Integer value;
    private String country;

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


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.getTimestamp() + ", " + this.getValue() + ", " + this.getCountry();
    }
}
