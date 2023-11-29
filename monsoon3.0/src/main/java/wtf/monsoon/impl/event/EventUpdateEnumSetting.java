package wtf.monsoon.impl.event;

import lombok.*;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.setting.Setting;

@Getter @Setter
public class EventUpdateEnumSetting extends Event {

    private Setting<Enum<?>> setting;
    private Enum<?> oldValue, newValue;

    public EventUpdateEnumSetting(Setting<Enum<?>> setting, Enum<?> oldValue, Enum<?> newValue) {
        setSetting(setting);
        setOldValue(oldValue);
        setNewValue(newValue);
    }

}
