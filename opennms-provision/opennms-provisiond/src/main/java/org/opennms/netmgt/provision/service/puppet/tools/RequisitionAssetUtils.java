package org.opennms.netmgt.provision.service.puppet.tools;

import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RequisitionAssetUtils {
    
    public static Collection generateRequisitionAssets(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<RequisitionAsset> requAssets = new ArrayList<RequisitionAsset>();
        
        String fieldName;
        Class fieldType;
        String getterName;
        Method getMethod;
        String assetName;
        
        Class[] methodParameters = new Class[1];

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getAnnotation(RequistionAssetGen.class) != null) {
                fieldName = field.getName();

                assetName = field.getAnnotation(RequistionAssetGen.class).assetName();
                if (assetName.equals("N/A")) {
                    assetName = fieldName;
                }

                getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                fieldType = field.getType();
                methodParameters[0] = fieldType;
                getMethod = object.getClass().getMethod(getterName);

                if (Modifier.isPublic(getMethod.getModifiers()) && getMethod.getReturnType().equals(String.class)) {
                    String getResult = (String) getMethod.invoke(object);
                    requAssets.add(new RequisitionAsset(assetName, getResult));
                }
            }
        }
        return requAssets;
    }
}
