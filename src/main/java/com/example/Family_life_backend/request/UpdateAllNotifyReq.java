package com.example.Family_life_backend.request;

import java.util.List;

public class UpdateAllNotifyReq {

	private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
