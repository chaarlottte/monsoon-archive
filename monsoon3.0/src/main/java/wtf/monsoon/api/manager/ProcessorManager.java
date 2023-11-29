package wtf.monsoon.api.manager;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.processor.Processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ProcessorManager {

    private final LinkedHashMap<Class<? extends Processor>, Processor> processors = new LinkedHashMap<>();

    public void addProcessor(Class<? extends Processor> clazz, Processor processor) {
        processors.put(clazz, processor);
        Wrapper.getEventBus().subscribe(processor);
    }

    public List<Processor> getProcessors() {
        return new ArrayList<>(this.processors.values());
    }

    public <T extends Processor> T getProcessor(Class<T> clazz) {
        return (T) processors.get(clazz);
    }

}
