package wtf.monsoon.api.setting;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventUpdateEnumSetting;

public class Setting<T> {

    @Getter private final String name;
    @Getter private String description = "";

    private Supplier<Boolean> visibility = () -> true;

    @Getter private Setting<?> parent = null;
    @Getter private List<Setting<?>> children = new ArrayList<>();

    @Getter protected T value;
    @Getter private T defaultValue;

    @Getter private T minimum;
    @Getter private T maximum;
    @Getter private T incrementation;

    @Getter private int index = 0;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;

        if (getValue() instanceof Enum<?>) {
            index = ((Enum<?>) value).ordinal();
        }
    }

    public void setValue(T value) {
        if (value instanceof Enum<?>) {
            EventUpdateEnumSetting event = new EventUpdateEnumSetting((Setting<Enum<?>>) this, (Enum<?>) this.value, (Enum<?>) value);

            Wrapper.getEventBus().post(event);

            if (!event.isCancelled()) {
                this.value = value;
            }
        } else {
            this.value = value;
        }
    }

    public void setValueSilent(T value) {
        this.value = value;
    }

    public T getMode(boolean previous) {
        if (getValue() instanceof Enum<?>) {
            Enum<?> enumeration = (Enum<?>) getValue();

            String[] values = Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);

            if (!previous) {
                index = index + 1 > values.length - 1 ? 0 : index + 1;
            } else {
                index = index - 1 < 0 ? values.length - 1 : index - 1;
            }

            return (T) Enum.valueOf(enumeration.getClass(), values[index]);
        }

        return null;
    }

    public Setting<T> minimum(T minimum) {
        this.minimum = minimum;
        return this;
    }

    public Setting<T> maximum(T maximum) {
        this.maximum = maximum;
        return this;
    }

    public Setting<T> incrementation(T incrementation) {
        this.incrementation = incrementation;
        return this;
    }

    public Setting<T> describedBy(String description) {
        this.description = description;
        return this;
    }

    public Setting<T> visibleWhen(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public Setting<T> childOf(Setting<?> parent) {
        this.parent = parent;
        this.parent.children.add(this);

        return this;
    }

    public String getPath() {
        return getParent() == null ? getName() : getParent().getPath() + getName();
    }

    public List<Setting<?>> getHierarchy() {
        List<Setting<?>> hierarchy = new ArrayList<>();

        for (Setting<?> subsetting : getChildren()) {
            hierarchy.add(subsetting);
            hierarchy.addAll(subsetting.getHierarchy());
        }

        return hierarchy;
    }

    public boolean isVisible() {
        return visibility.get();
    }

}
