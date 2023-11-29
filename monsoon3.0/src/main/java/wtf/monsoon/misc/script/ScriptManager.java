package wtf.monsoon.misc.script;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import me.surge.api.Coercer;
import me.surge.api.annotation.ExcludeFromProcessing;
import me.surge.api.result.Failure;
import me.surge.api.result.Result;
import me.surge.api.result.Success;
import me.surge.lexer.error.impl.RuntimeError;
import me.surge.lexer.symbol.SymbolTable;
import me.surge.lexer.value.FunctionData;
import me.surge.lexer.value.ListValue;
import me.surge.lexer.value.Value;
import me.surge.lexer.value.method.BaseMethodValue;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.misc.script.wrapper.ScriptUtil;

import java.util.ArrayList;
import java.util.Collections;

public class ScriptManager {

    public static void registerModule(Value container) {
        Value init = container.getSymbols().get("init");

        if (init instanceof BaseMethodValue) {
            init.execute(new ArrayList<>());
        }

        Module module = new Module(container.getSymbols().get("name").stringValue(), container.getSymbols().get("description").stringValue(), Category.SCRIPT) {
            @Override
            public void onEnable() {
                super.onEnable();

                Value method = container.getSymbols().get("onEnable");

                if (method instanceof BaseMethodValue) {
                    method.execute(new ArrayList<>());
                }
            }

            @Override
            public void onDisable() {
                super.onDisable();

                Value method = container.getSymbols().get("onDisable");

                if (method instanceof BaseMethodValue) {
                    method.execute(new ArrayList<>());
                }
            }

            @EventLink
            public final Listener<EventUpdate> eventUpdateListener = e -> {
                Value method = container.getSymbols().get("onUpdate");

                if (method instanceof BaseMethodValue) {
                    method.execute(new ArrayList<>());
                }
            };

            @EventLink
            public final Listener<EventRender2D> eventRender2DListener = e -> {
                Value method = container.getSymbols().get("onRender2D");

                if (method instanceof BaseMethodValue) {
                    method.execute(new ArrayList<>());
                }
            };

            @EventLink
            public final Listener<EventPacket> eventPacketListener = e -> {
                Value method = container.getSymbols().get("onPacket");

                if (method instanceof BaseMethodValue) {
                    method.execute(new ArrayList<>(Collections.singleton(Coercer.coerceObject(e.getPacket()))));
                }
            };
        };

        ScriptUtil.settings.forEach((mod, set) -> {
            if (mod.toLowerCase().contains(module.getName().toLowerCase())) {
                module.getSettings().add(set);
            }
        });

        Wrapper.getMonsoon().getModuleManager().putModule(module.getClass(), module);
        Wrapper.getLogger().info("Added module " + module.getName() + " from a script.");
    }

    public static Result registerModules(FunctionData functionData, ListValue values) {
        for (Value element : values.getElements()) {
            registerModule(element);
        }

        return new Success(null);
    }

}
