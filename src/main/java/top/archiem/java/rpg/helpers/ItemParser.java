package top.archiem.java.rpg.helpers;

import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;

public class ItemParser {

    /**
     * Serializes an item to "name:TYPE:value:goldValue".
     */
    public static String serialize(Item item) {
        return item.getName() + ":" + item.getItemType().toString() + ":" + item.getValue() + ":" + item.getGoldValue();
    }

    /**
     * Parses an item from already-split parts, reading name, TYPE, value, goldValue
     * starting at index {@code start}.
     *
     * @throws IllegalArgumentException if parts are missing or malformed
     */
    public static Item parseViaParts(String[] parts, int start) {
        if (parts == null || start < 0 || parts.length < start + 4) {
            throw new IllegalArgumentException("Not enough parts to parse an item starting at index " + start);
        }

        String name = parts[start];
        String typeStr = parts[start + 1].trim();

        ItemTypes type;
        try {
            type = ItemTypes.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown item type '" + typeStr + "'", e);
        }

        int value;
        int goldValue;
        try {
            value = Integer.parseInt(parts[start + 2].trim());
            goldValue = Integer.parseInt(parts[start + 3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in item parts", e);
        }

        return new Item(type, value, name, goldValue);
    }

    /**
     * Parses a string in the form "name:TYPE:value:goldValue" back into an Item.
     *
     * @throws IllegalArgumentException if the string is malformed
     */
    public static Item parse(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Item data is null or empty");
        }

        int lastColon = data.lastIndexOf(':');
        int secondLastColon = lastColon > 0 ? data.lastIndexOf(':', lastColon - 1) : -1;
        int thirdLastColon = secondLastColon > 0 ? data.lastIndexOf(':', secondLastColon - 1) : -1;

        if (thirdLastColon < 0) {
            throw new IllegalArgumentException("Invalid item format (expected name:TYPE:value:goldValue): " + data);
        }

        String name = data.substring(0, thirdLastColon);
        String typeStr = data.substring(thirdLastColon + 1, secondLastColon).trim();
        String valueStr = data.substring(secondLastColon + 1, lastColon).trim();
        String goldStr = data.substring(lastColon + 1).trim();

        ItemTypes type;
        try {
            type = ItemTypes.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown item type '" + typeStr + "' in: " + data, e);
        }

        int value;
        int goldValue;
        try {
            value = Integer.parseInt(valueStr);
            goldValue = Integer.parseInt(goldStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in item data: " + data, e);
        }

        return new Item(type, value, name, goldValue);
    }
}