/* * *************************************************************************************
' Script Name: ConvertUtils.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the main routine Minecraft Bot.
' @(#)    Here we simply instantion the bot and call it's 'start()' routine
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2020-01-20 Initial Version
' 
' ************************************************************************************** */

/*
 * Orginal Code Forked from code created by David Luedtke (MrKinau)
 * 2019/5/3
 */
package org.geekwisdom.gwmcbot.io;

import org.geekwisdom.gwmcbot.GWmcbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PropertyProcessor {

    private Map<String, Properties> props = new HashMap<>();
    private Map<String, List<Map<String, Field>>> undefinedKeys = new HashMap<>();

    public void processAnnotations(final Object object, String dir, String comments) throws AnnotationFormatError, IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<Field> fields = ReflectionUtils.getAllFields(object);
        for (Field field : fields) {
            if(!field.isAnnotationPresent(Property.class))
                continue;
            Property propAnnotation = field.getAnnotation(Property.class);

            String key = propAnnotation.key().trim();
            String source = dir.trim();

            if(key.isEmpty() || source.isEmpty())
                throw new AnnotationFormatError("Property Annotation needs source and key");

            File file = new File(dir);
            if(!file.exists()) {
                file.createNewFile();
            }

            //check if the source file is not already loaded
            if (!props.containsKey(source)) {
                Properties properties = new Properties();
                properties.load(new FileInputStream(file));
                props.put(source, properties);
            }

            String value = props.get(source).getProperty(key);
            if(value == null) {
                if(undefinedKeys.get(source) == null) {
                    Map<String, Field> kvPair = new HashMap<>();
                    kvPair.put(key, field);
                    List<Map<String, Field>> kvPairList = new ArrayList<>();
                    kvPairList.add(kvPair);
                    undefinedKeys.put(source, kvPairList);
                    GWmcbot.getLog().warning("Undefined config option in " + source + " -> " + key);
                    continue;
                } else {
                    List<Map<String, Field>> undKeys = undefinedKeys.get(source);
                    Map<String, Field> kvPair = new HashMap<>();
                    kvPair.put(key, field);
                    undKeys.add(kvPair);
                    undefinedKeys.put(source, undKeys);
                    GWmcbot.getLog().warning("Undefined config option in " + source + " -> " + key);
                    continue;
                }
            }

            Object typedValue = ConvertUtils.convert(value, field.getType());
            if(typedValue == null)
                throw new ConvertException("Cannot convert type from " + field.getName() + ":" + field.getType().getSimpleName());
            ReflectionUtils.setField(field, object, typedValue);
        }
        for (String source : props.keySet()) {
            if(undefinedKeys.containsKey(source)) {
                for (Map<String, Field> configEntry : undefinedKeys.get(source)) {
                    configEntry.forEach((key, value) -> {
                        props.get(source).setProperty(key, Objects.requireNonNull(ReflectionUtils.getField(value, object)).toString());
                    });
                }
                props.get(source).store(new FileOutputStream(new File(dir)), comments);
            }
        }
    }
}
