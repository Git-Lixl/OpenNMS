package org.opennms.netmgt.provision.service.puppet.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value= ElementType.FIELD)
public @interface Map2Bean {String mapKeyName() default "N/A";}