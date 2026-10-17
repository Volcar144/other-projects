package top.archiem.java.rpg.helpers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import top.archiem.java.rpg.AnsiColors;

/**
 * Terminal ASCII art and simple frame animations.
 *
 * Static art is printed through {@link ConsoleTyper}. Frame animations need
 * precise control (cursor movement, no per-char delay), so they write to
 * stdout directly. If the terminal can't move the cursor (no console, TERM=dumb,
 * or JARPG_ANIMATIONS=off), frames are printed sequentially instead.
 */
public final class Animations {

    private static final String ESC = "\u001B[";
    private static final PrintStream OUT = System.out;
    private static final boolean CURSOR_SUPPORTED = detectCursorSupport();

    private static final int FRAME_MS = 70;
    private static final int HEAVY_FRAME_MS = 90;
    private static final int FLASH_MS = 80;
    private static final int DRAIN_MS = 60;
    private static final int BAR_WIDTH = 20;

    private Animations() {
    }

    // ---------------------------------------------------------------
    // Art
    // ---------------------------------------------------------------
    public static final String[] TITLE_BANNER = {
            "   _       ____              ",
            "  (_) __ _|  _ \\ _ __   __ _ ",
            "  | |/ _` | |_) | '_ \\ / _` |",
            "  | | (_| |  _ <| |_) | (_| |",
            " _/ |\\__,_|_| \\_\\ .__/ \\__, |",
            "|__/            |_|    |___/ ",
    };

    public static final String[] GOBLIN = {
            "  ,      ,  ",
            " /(.-\"\"-.)\\ ",
            " \\  o  o  / ",
            "  \\  ^^  /  ",
            "  /`----'\\  ",
            " /  |  |  \\ ",
    };

    public static final String[] SKELETON = {
            "    .-.    ",
            "   (x.x)   ",
            "  __|=|__  ",
            " /  |=|  \\ ",
            "    |=|    ",
            "   _/ \\_   ",
    };

    public static final String[] ZOMBIE = {
            "   _____   ",
            "  | x o |  ",
            "  |  ~  |  ",
            "   \\___/   ",
            " --|   |-- ",
            "   /   \\   ",
    };

    public static final String[] SPIDER = {
            " /\\  .-.  /\\ ",
            "//\\\\/o o\\//\\\\",
            "//  \\ ^ /  \\\\",
            "    /`-'\\    ",
    };

    public static final String[] GHOST = {
            "  .-\"\"-.  ",
            " / o  o \\ ",
            "|   __   |",
            "|  (__)  |",
            "|        |",
            "'^^'^^'^^'",
    };

    public static final String[] SLIME = {
            "   .---.   ",
            " .' o o '. ",
            "(   ___   )",
            " '-------' ",
    };

    public static final String[] MAGE = {
            "    /\\    ",
            "   /**\\   ",
            "  /____\\  ",
            "  (o  o)  ",
            " __\\--/__ ",
            "/  |/\\|  \\",
    };

    public static final String[] KNIGHT = {
            "  ,====.  ",
            "  |-||-|  ",
            "  |____|  ",
            " /|=||=|\\ ",
            "| |=||=| |",
            "  /    \\  ",
    };

    /** Generic fallback sprite for unknown enemies. */
    public static final String[] GENERIC_ENEMY = GOBLIN;

    public static final String[] SLASH_FRAMES = {
            frame("           *",
                  "         //",
                  "",
                  "",
                  ""),
            frame("           *",
                  "         //",
                  "       //",
                  "     //",
                  ""),
            frame("           *",
                  "         //",
                  "       //",
                  "     //",
                  "   //"),
            frame("     \\  *  /",
                  "      \\// /",
                  "    -- ** --",
                  "     //  \\",
                  "   //"),
    };

    public static final String[] HEAVY_SLASH_FRAMES = {
            frame("            **",
                  "         ///",
                  "",
                  "",
                  "",
                  ""),
            frame("            **",
                  "         ///",
                  "       ///",
                  "     ///",
                  "",
                  ""),
            frame("  \\\\\\       **",
                  "    \\\\\\  ///",
                  "      \\X//",
                  "     ///\\\\\\",
                  "   ///    \\\\\\",
                  " **"),
            frame("  \\   \\  |  /   /",
                  "    \\  \\ | /  /",
                  " ---=== XX ===---",
                  "    /  / | \\  \\",
                  "  /   /  |  \\   \\",
                  ""),
    };

    public static final String[] HIT_FLASH_FRAMES = {
            AnsiColors.YELLOW + AnsiColors.BOLD + frame(
                  "   \\ | /",
                  " -- HIT --",
                  "   / | \\") + AnsiColors.RESET,
            AnsiColors.RED + AnsiColors.BOLD + frame(
                  " \\  \\|/  /",
                  "--- HIT ---",
                  " /  /|\\  \\") + AnsiColors.RESET,
            AnsiColors.YELLOW + frame(
                  "    .",
                  "   hit",
                  "    '") + AnsiColors.RESET,
    };

    /** Maps an enemy name (from world.txt) to a sprite, falling back to a generic one. */
    public static String[] artFor(String enemyName) {
        String n = enemyName == null ? "" : enemyName.toLowerCase(Locale.ROOT);
        if (n.contains("skeleton") || n.contains("bone")) return SKELETON;
        if (n.contains("zombie") || n.contains("ghoul")) return ZOMBIE;
        if (n.contains("spider")) return SPIDER;
        if (n.contains("ghost") || n.contains("spectral") || n.contains("spirit") || n.contains("wraith")) return GHOST;
        if (n.contains("slime")) return SLIME;
        if (n.contains("mage") || n.contains("wizard") || n.contains("witch")) return MAGE;
        if (n.contains("knight")) return KNIGHT;
        if (n.contains("goblin")) return GOBLIN;
        return GENERIC_ENEMY;
    }

    // ---------------------------------------------------------------
    // Low-level helpers
    // ---------------------------------------------------------------
    public static boolean cursorSupported() {
        return CURSOR_SUPPORTED;
    }

    public static void clearScreen() {
        if (!CURSOR_SUPPORTED) return;
        OUT.print(ESC + "2J" + ESC + "H");
        OUT.flush();
    }

    public static void sleep(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Plays frames in place, leaving the last frame on screen. */
    public static void playFrames(String[] frames, int frameDelayMs) {
        playFrames(frames, frameDelayMs, false);
    }

    /**
     * Plays frames in place by moving the cursor back up over the previous
     * frame. Without cursor support, frames are printed one after another.
     */
    public static void playFrames(String[] frames, int frameDelayMs, boolean eraseAfter) {
        for (int i = 0; i < frames.length; i++) {
            String frame = frames[i];
            OUT.println(frame);
            OUT.flush();
            sleep(frameDelayMs);
            boolean last = i == frames.length - 1;
            if (CURSOR_SUPPORTED && (!last || eraseAfter)) {
                eraseLinesAbove(lineCount(frame));
            }
        }
    }

    // ---------------------------------------------------------------
    // Game-facing effects
    // ---------------------------------------------------------------

    /** Reveals the title banner line by line. */
    public static void titleScreen(ConsoleTyper tp) {
        ConsoleTyper.Speed previous = tp.getSpeed();
        tp.setSpeed(ConsoleTyper.Speed.INSTANT);
        tp.blank();
        for (String line : TITLE_BANNER) {
            tp.println(AnsiColors.CYAN + AnsiColors.BOLD + line + AnsiColors.RESET);
            sleep(FRAME_MS);
        }
        tp.println(AnsiColors.yellow("      ~ an interactive dungeon crawler ~"));
        tp.blank();
        tp.setSpeed(previous);
    }

    /** Prints sprites for the given enemies side by side (3 per row) with names underneath. */
    public static void showEnemies(ConsoleTyper tp, List<String> names) {
        ConsoleTyper.Speed previous = tp.getSpeed();
        tp.setSpeed(ConsoleTyper.Speed.INSTANT);
        for (int start = 0; start < names.size(); start += 3) {
            List<String> row = names.subList(start, Math.min(start + 3, names.size()));
            for (String line : sideBySide(row)) {
                tp.println(line);
            }
            tp.blank();
        }
        tp.setSpeed(previous);
    }

    public static void slash() {
        playFrames(SLASH_FRAMES, FRAME_MS, true);
        playFrames(HIT_FLASH_FRAMES, FRAME_MS, true);
    }

    public static void heavySlash() {
        playFrames(HEAVY_SLASH_FRAMES, HEAVY_FRAME_MS, true);
        playFrames(HIT_FLASH_FRAMES, HEAVY_FRAME_MS, true);
    }

    /** Animates an HP bar draining from {@code fromHp} to {@code toHp}, ending on the final bar. */
    public static void drainBar(int fromHp, int toHp, int maxHp) {
        if (!CURSOR_SUPPORTED || fromHp <= toHp) {
            OUT.println(renderBar(toHp, maxHp));
            OUT.flush();
            return;
        }
        drainInPlace(fromHp, toHp, maxHp, "", "");
        OUT.println();
        OUT.flush();
    }

    /** Flashes a red-bordered HP bar, then drains it to the new value. */
    public static void damageFlash(int fromHp, int toHp, int maxHp) {
        String border = AnsiColors.RED + AnsiColors.BOLD;
        if (!CURSOR_SUPPORTED) {
            OUT.println(border + "!! " + renderBar(toHp, maxHp) + " !!" + AnsiColors.RESET);
            OUT.flush();
            return;
        }
        String bar = renderBar(fromHp, maxHp);
        for (int i = 0; i < 3; i++) {
            rewriteLine(border + "!! " + bar + " !!" + AnsiColors.RESET);
            sleep(FLASH_MS);
            rewriteLine("   " + bar);
            sleep(FLASH_MS);
        }
        drainInPlace(fromHp, toHp, maxHp, border + "!! ", " !!" + AnsiColors.RESET);
        OUT.println();
        OUT.flush();
    }

    /** Same format as {@code Entity.hpBar()}. */
    public static String renderBar(int hp, int maxHp) {
        int clamped = Math.max(0, Math.min(hp, maxHp));
        int filled = maxHp <= 0 ? 0 : (int) Math.round(((double) clamped / maxHp) * BAR_WIDTH);
        if (clamped == maxHp) filled = BAR_WIDTH;
        if (clamped == 0) filled = 0;
        return "[" + "\u2588".repeat(filled) + "\u2591".repeat(BAR_WIDTH - filled) + "] " + clamped + "/" + maxHp;
    }

    // ---------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------
    private static boolean detectCursorSupport() {
        String flag = System.getenv("JARPG_ANIMATIONS");
        if (flag != null && (flag.equalsIgnoreCase("off") || flag.equals("0") || flag.equalsIgnoreCase("false"))) {
            return false;
        }
        if ("dumb".equalsIgnoreCase(System.getenv("TERM"))) {
            return false;
        }
        return System.console() != null;
    }

    private static void drainInPlace(int fromHp, int toHp, int maxHp, String prefix, String suffix) {
        int steps = 8;
        for (int s = 1; s <= steps; s++) {
            int hp = fromHp - (int) Math.round((fromHp - toHp) * (s / (double) steps));
            rewriteLine(prefix + renderBar(hp, maxHp) + suffix);
            sleep(DRAIN_MS);
        }
    }

    private static void rewriteLine(String content) {
        OUT.print("\r" + ESC + "2K" + content);
        OUT.flush();
    }

    private static void eraseLinesAbove(int lines) {
        OUT.print(ESC + lines + "A\r" + ESC + "J");
        OUT.flush();
    }

    private static int lineCount(String s) {
        return s.split("\n", -1).length;
    }

    private static String frame(String... lines) {
        return String.join("\n", lines);
    }

    private static List<String> sideBySide(List<String> names) {
        List<String[]> sprites = new ArrayList<>();
        int height = 0;
        for (String name : names) {
            String[] art = artFor(name);
            sprites.add(art);
            height = Math.max(height, art.length);
        }

        String gap = "    ";
        List<String> out = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sprites.size(); i++) {
                String[] art = sprites.get(i);
                int width = columnWidth(art, names.get(i));
                int offset = height - art.length;
                String line = row >= offset ? art[row - offset] : "";
                sb.append(AnsiColors.RED).append(pad(line, width)).append(AnsiColors.RESET);
                if (i < sprites.size() - 1) sb.append(gap);
            }
            out.add(sb.toString());
        }

        StringBuilder labels = new StringBuilder();
        for (int i = 0; i < sprites.size(); i++) {
            int width = columnWidth(sprites.get(i), names.get(i));
            labels.append(AnsiColors.YELLOW).append(center(names.get(i), width)).append(AnsiColors.RESET);
            if (i < sprites.size() - 1) labels.append(gap);
        }
        out.add(labels.toString());
        return out;
    }

    private static int columnWidth(String[] art, String name) {
        int width = name.length();
        for (String line : art) width = Math.max(width, line.length());
        return width;
    }

    private static String pad(String s, int width) {
        return s + " ".repeat(Math.max(0, width - s.length()));
    }

    private static String center(String s, int width) {
        int left = Math.max(0, (width - s.length()) / 2);
        return pad(" ".repeat(left) + s, width);
    }
}
