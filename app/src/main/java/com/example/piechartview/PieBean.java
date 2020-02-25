package com.example.piechartview;

public class PieBean {
    private float number;
    private int colorRes;
    private boolean isSelected;
    private float startC;
    private float endC;


    public PieBean(float number, int colorRes, boolean isSelected) {
        this.number = number;
        this.colorRes = colorRes;
        this.isSelected = isSelected;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public int getColorRes() {
        return colorRes;
    }

    public void setColorRes(int colorRes) {
        this.colorRes = colorRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getStartC() {
        return startC;
    }

    public void setStartC(float startC) {
        this.startC = startC;
    }

    public float getEndC() {
        return endC;
    }

    public void setEndC(float endC) {
        this.endC = endC;
    }
}