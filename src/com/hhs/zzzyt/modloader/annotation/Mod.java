package com.hhs.zzzyt.modloader.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
	String modid();
	String name();
	String version();
}
