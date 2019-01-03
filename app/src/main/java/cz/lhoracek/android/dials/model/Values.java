package cz.lhoracek.android.dials.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by lhoracek on 19/11/16.
 */

@AutoValue
public abstract class Values {
    public static TypeAdapter<Values> typeAdapter(Gson gson) {
        return new AutoValue_Values.GsonTypeAdapter(gson);
    }

    public abstract Integer getGear();
    public abstract Integer getOdo();
    public abstract Integer getRpm();
    public abstract Float   getVoltage();
    public abstract Float   getOilTemp();
    public abstract Float   getFuel();
    public abstract Float   getTemp();
    public abstract Integer getSpeed();
    public abstract Boolean getTurnlight();
    public abstract Boolean getNeutral();
    public abstract Boolean getEngine();
    public abstract Boolean getLowBeam();
    public abstract Boolean getHighBeam();

    public static Values create(int gear,
                         int odo,
                         int rpm,
                         float voltage,
                         float oilTemp,
                         float fuel,
                         float temp,
                         int speed,
                         boolean turnlight,
                         boolean neutral,
                         boolean engine,
                         boolean lowBeam,
                         boolean highBeam){
        return new AutoValue_Values(gear, odo, rpm, voltage, oilTemp, fuel, temp, speed, turnlight, neutral, engine, lowBeam, highBeam);
    }
}
