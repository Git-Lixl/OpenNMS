package org.opennms.netmgt.provision.service.puppet.tools;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Map2BeanUtils {

    public static Object fill(Object object, Map map) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String fieldName;
        Class fieldType;
        String setterName;
        Method setMethod;
        String mapKeyName;
        Class[] methodParameters = new Class[1];

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Map2Bean.class) != null) {
                fieldName = field.getName();

                mapKeyName = field.getAnnotation(Map2Bean.class).mapKeyName();
                if (mapKeyName.equals("N/A")) {
                    mapKeyName = fieldName;
                }

                setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                fieldType = field.getType();
                methodParameters[0] = fieldType;
                setMethod = object.getClass().getMethod(setterName, methodParameters);

                if (Modifier.isPublic(setMethod.getModifiers()) && setMethod.getReturnType().equals(void.class)) {
                    if (map.containsKey(mapKeyName)) {
                        setMethod.invoke(object, map.get(mapKeyName));
                    }
                }
            }
        }
        return object;
    }
}
