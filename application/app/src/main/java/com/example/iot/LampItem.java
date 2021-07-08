package com.example.iot;

public class LampItem {
    private String name, code;
    Double temperature;
    private Double gas = 0.0;
    int humid;

    public Double getGas() {
        return gas;
    }

    public void setGas(Double gas) {
        this.gas = gas;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumid(int humid) {
        this.humid = humid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LampItem(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
