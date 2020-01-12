/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/6
 */

package org.geekwisdom.gwmcbot.network.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TextComponent {

    public static String toPlainText(JsonObject object) throws IllegalStateException {
        StringBuilder messageBuilder = new StringBuilder();

        if(object.has("with"))  {
            JsonArray array = object.getAsJsonArray("with");
            for(int i = 0; i < array.size(); i++) {
                messageBuilder = new StringBuilder(getText(array.get(i), messageBuilder) + " ");
            }
            return messageBuilder.toString();
        } else {
            return getText(object, messageBuilder);
        }
    }

    private static String getText(JsonElement object, StringBuilder messageBuilder) {
        if(object.isJsonPrimitive()) {
            messageBuilder.append(object.getAsString());
            return messageBuilder.toString();
        }

        JsonObject jObject = object.getAsJsonObject();

        if (jObject.has("text")) {
            String text = jObject.get("text").getAsString();
            if (!text.isEmpty()) messageBuilder.append(text);
        }

        if (jObject.has("extra") && jObject.get("extra").isJsonArray()) {
            JsonArray extras = jObject.getAsJsonArray("extra");

            for (int i = 0; i < extras.size(); i++) {
                if(extras.get(i).isJsonObject()) {
                    JsonObject extraObject = extras.get(i).getAsJsonObject();

                    if (extraObject.has("text")) {
                        String text = extraObject.get("text").getAsString();
                        if (!text.isEmpty()) messageBuilder.append(text);
                    }
                } else {
                    messageBuilder.append(extras.get(i).getAsString());
                }
            }
        }
        return messageBuilder.toString();
    }
}
