package io.github.appliedenergistics.metrics.fabric.network;

import net.minecraft.network.NetworkState;

import java.lang.reflect.Field;
import java.util.Map;

public class GenerateNicePacketNames {

    public static void main(String[] args) throws Exception {

        Field handlerStateMapField = NetworkState.class.getDeclaredField("HANDLER_STATE_MAP");
        handlerStateMapField.setAccessible(true);

        Map<?, ?> handlerStateMap = (Map<?, ?>) handlerStateMapField.get(null);

        for (Object handlerStateMapKey : handlerStateMap.keySet()) {
            Class<?> packetClass = (Class<?>) handlerStateMapKey;
            String name;
            Class<?> enclosingClass = packetClass.getEnclosingClass();
            if (enclosingClass != null) {
                name = mangleName(enclosingClass.getSimpleName()) + '_'
                        + mangleName(packetClass.getSimpleName());
            } else {
                name = mangleName(packetClass.getSimpleName());
            }
            System.out.println(".put(" + packetClass.getName().replace('$', '.') + ".class, \"" + name + "\")");
        }

    }

    private static String mangleName(String name) {
        name = name.replace("C2SPacket", "");
        name = name.replace("S2CPacket", "");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch) && result.length() > 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(ch));
        }
        return result.toString();
    }

}
