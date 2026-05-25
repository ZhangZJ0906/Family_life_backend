package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.entity.Medicine;

public class MedicineRes {

	private Integer code;
    private String message;
    private List<Medicine> data;

    public MedicineRes(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public MedicineRes(Integer code, String message, List<Medicine> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() { return code; }
    public String getMessage() { return message; }
    public List<Medicine> getData() { return data; }
}
