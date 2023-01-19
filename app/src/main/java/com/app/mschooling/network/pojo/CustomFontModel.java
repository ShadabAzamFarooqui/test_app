package com.app.mschooling.network.pojo;

public class CustomFontModel {
    public Boolean isSelected;
    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean setSelected(Boolean selected) {
        isSelected = selected;
        return true;
    }
}