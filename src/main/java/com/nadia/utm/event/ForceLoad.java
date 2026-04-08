package com.nadia.utm.event;

import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;

/**
 * Loads target class immediately apon mod start.
 * Mandatory for {@link utmEvents#register(Class, Consumer)} bound events.
 * Otherwise useful for registry/sanity reasons.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ForceLoad {
    Class<?> value() default Object.class;
    Dist dist() default Dist.DEDICATED_SERVER;
}