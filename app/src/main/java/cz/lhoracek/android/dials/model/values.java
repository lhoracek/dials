package cz.lhoracek.android.dials.model;

import com.google.auto.value.AutoValue;

/**
 * Created by horaclu2 on 19/11/16.
 */

@AutoValue
public class Values {
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
}
