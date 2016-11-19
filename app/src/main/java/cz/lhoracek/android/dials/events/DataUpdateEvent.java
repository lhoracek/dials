package cz.lhoracek.android.dials.events;

/**
 * Created by horaclu2 on 19/11/16.
 */

public class DataUpdateEvent {
    private Integer revs;

    public DataUpdateEvent(Integer revs) {
        this.revs = revs;
    }

    public Integer getRevs() {
        return revs;
    }
}
