package top.archiem.java.rpg.errors;

public class SaveLoadException extends RuntimeException {
    public SaveLoadException(String message, boolean load) {
        super("An error occurred while trying to " + (load ? "load" : "save") + " from a file. See further details: " + message);
    }
}
