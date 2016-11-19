package cz.lhoracek.android.dials.model;

import android.databinding.BaseObservable;

/**
 * Created by horaclu2 on 19/11/16.
 */

public class DialsModel extends BaseObservable {
    private Integer gear;
    private Integer revs;
    private Float   voltage;
    private Float   oilTemp;
    private Float   fuel;
    private Float   temp;
    private Integer speed;
    private Boolean turnlight;
    private Boolean neutral;
    private Boolean engine;
    private Boolean lowBeam;
    private Boolean highBeam;

    public Boolean getEngine() {
        return engine;
    }

    public void setEngine(Boolean engine) {
        this.engine = engine;
        notifyChange();
    }

    public Float getFuel() {
        return fuel;
    }

    public void setFuel(Float fuel) {
        this.fuel = fuel;
        notifyChange();
    }

    public Integer getGear() {
        return gear;
    }

    public void setGear(Integer gear) {
        this.gear = gear;
        notifyChange();
    }

    public Boolean getHighBeam() {
        return highBeam;
    }

    public void setHighBeam(Boolean highBeam) {
        this.highBeam = highBeam;
        notifyChange();
    }

    public Boolean getLowBeam() {
        return lowBeam;
    }

    public void setLowBeam(Boolean lowBeam) {
        this.lowBeam = lowBeam;
        notifyChange();
    }

    public Boolean getNeutral() {
        return neutral;
    }

    public void setNeutral(Boolean neutral) {
        this.neutral = neutral;
        notifyChange();
    }

    public Float getOilTemp() {
        return oilTemp;
    }

    public void setOilTemp(Float oittemp) {
        this.oilTemp = oittemp;
        notifyChange();
    }

    public Integer getRevs() {
        return revs;
    }

    public void setRevs(Integer revs) {
        this.revs = revs;
        notifyChange();
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
        notifyChange();
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
        notifyChange();
    }

    public Boolean getTurnlight() {
        return turnlight;
    }

    public void setTurnlight(Boolean turnlight) {
        this.turnlight = turnlight;
        notifyChange();
    }

    public Float getVoltage() {
        return voltage;
    }

    public void setVoltage(Float voltage) {
        this.voltage = voltage;
        notifyChange();
    }
}
