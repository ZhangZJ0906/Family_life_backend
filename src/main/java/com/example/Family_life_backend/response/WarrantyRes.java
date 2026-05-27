package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.entity.Warranty;

public class WarrantyRes {


    private Integer code;
    private String message;
    private List<Warranty> data;

    public WarrantyRes(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public WarrantyRes(Integer code, String message, List<Warranty> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Warranty> getData() {
        return data;
    }
}
