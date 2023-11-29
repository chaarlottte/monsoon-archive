package wtf.monsoon.impl.module.annotation;

import wtf.monsoon.api.setting.Bind;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultBind {

    int code();
    Bind.Device device();

}
