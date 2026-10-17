package top.archiem.java.rpg.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import top.archiem.java.rpg.AnsiColors;

/**
 * Typewriter-style console printer for the RPG.
 *
 * Features:
 *  - Typewriter effect with adjustable speed and punctuation pauses
 *  - Inline color tags:   "{red}Danger!{reset} You see a {bold}dragon{reset}."
 *  - Inline pause tags:   "The door creaks{pause:800}... then opens."
 *  - Raw AnsiColors output also works: print(AnsiColors.red("Danger!"))
 *  - Word wrapping at a set column width
 *  - Press Enter during typing to skip the rest of the current print call
 */
public class ConsoleTyper {

    // ---------------------------------------------------------------
    // Speed presets
    // ---------------------------------------------------------------
    public enum Speed {
        INSTANT(0),
        FAST(12),
        NORMAL(28),
        SLOW(55),
        DRAMATIC(90);

        final int msPerChar;

        Speed(int msPerChar) {
            this.msPerChar = msPerChar;
        }
    }

    // ---------------------------------------------------------------
    // Tag names usable in text, e.g. {red}, {bold}, {reset}
    // ---------------------------------------------------------------
    private static final Map<String, String> STYLES = new HashMap<>();

    static {
        STYLES.put("reset", AnsiColors.RESET);
        STYLES.put("bold", AnsiColors.BOLD);
        STYLES.put("underline", AnsiColors.UNDERLINE);
        STYLES.put("red", AnsiColors.RED);
        STYLES.put("green", AnsiColors.GREEN);
        STYLES.put("yellow", AnsiColors.YELLOW);
        STYLES.put("blue", AnsiColors.BLUE);
        STYLES.put("magenta", AnsiColors.MAGENTA);
        STYLES.put("cyan", AnsiColors.CYAN);
        STYLES.put("white", AnsiColors.WHITE);
    }

    /**
     * Matches either:
     *  - a raw ANSI SGR code such as \u001B[31m (from AnsiColors), or
     *  - a markup tag such as {red}, {reset}, or {pause:500}
     */
    private static final Pattern MARKUP =
            Pattern.compile("\u001B\\[[0-9;]*m|\\{(\\w+)(?::(\\d+))?\\}");

    // ---------------------------------------------------------------
    // Internal token model
    // ---------------------------------------------------------------
    private enum TokenType { CHAR, STYLE, PAUSE }

    private static final class Token {
        final TokenType type;
        final char ch;        // CHAR
        final String code;    // STYLE (zero-width escape sequence)
        final long pauseMs;   // PAUSE

        private Token(TokenType type, char ch, String code, long pauseMs) {
            this.type = type;
            this.ch = ch;
            this.code = code;
            this.pauseMs = pauseMs;
        }

        static Token ofChar(char c)       { return new Token(TokenType.CHAR, c, null, 0); }
        static Token ofStyle(String code) { return new Token(TokenType.STYLE, '\0', code, 0); }
        static Token ofPause(long ms)     { return new Token(TokenType.PAUSE, '\0', null, ms); }
    }

    // ---------------------------------------------------------------
    // State
    // ---------------------------------------------------------------
    private final PrintStream out;
    private final InputStream in;
    private final Random random = new Random();

    private Speed speed = Speed.NORMAL;
    private int wrapWidth = 80;          // 0 = no wrapping
    private boolean skipEnabled = true;

    private int column = 0;              // current visible column
    private boolean skipping = false;    // true once Enter was pressed

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------
    public ConsoleTyper() {
        this(System.out, System.in);
    }

    public ConsoleTyper(PrintStream out, InputStream in) {
        this.out = out;
        this.in = in;
    }

    // ---------------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------------
    public ConsoleTyper setSpeed(Speed speed) {
        this.speed = speed;
        return this;
    }

    public Speed getSpeed() {
        return speed;
    }

    public ConsoleTyper setWrapWidth(int width) {
        this.wrapWidth = Math.max(0, width);
        return this;
    }

    public ConsoleTyper setSkipEnabled(boolean enabled) {
        this.skipEnabled = enabled;
        return this;
    }

    // ---------------------------------------------------------------
    // Public printing API
    // ---------------------------------------------------------------

    /** Types text out without a trailing newline. */
    public synchronized void print(String text) {
        skipping = false;
        List<Token> tokens = parse(text);

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            switch (t.type) {
                case STYLE:
                    out.print(t.code);
                    break;
                case PAUSE:
                    if (!skipping) sleep(t.pauseMs);
                    checkForSkip();
                    break;
                case CHAR:
                    handleChar(tokens, i);
                    break;
            }
        }
        out.flush();
    }

    /** Types text out followed by a newline. */
    public synchronized void println(String text) {
        print(text);
        newline();
    }

    /** Just a newline. */
    public synchronized void println() {
        newline();
    }

    /** Prints a blank line. */
    public synchronized void blank() {
        newline();
    }

    /** Prints "Speaker: text" with the speaker name in bold yellow. */
    public synchronized void dialogue(String speaker, String text) {
        print(AnsiColors.BOLD + AnsiColors.YELLOW + speaker + ":" + AnsiColors.RESET + " ");
        println(text);
    }

    /** Prints a simple title banner in cyan. */
    public synchronized void banner(String title) {
        String line = repeat('=', Math.max(title.length() + 4, 10));
        println(AnsiColors.CYAN + line + AnsiColors.RESET);
        println(AnsiColors.CYAN + "  " + AnsiColors.BOLD + title + AnsiColors.RESET);
        println(AnsiColors.CYAN + line + AnsiColors.RESET);
    }

    /** Prints a prompt and waits for Enter. Reads from the same input stream. */
    public synchronized void waitForEnter(String prompt) {
        print(prompt);
        out.flush();
        try {
            int b;
            while ((b = in.read()) != -1 && b != '\n') {
                // consume until newline
            }
        } catch (IOException e) {
            // ignore
        }
        newline();
    }

    // ---------------------------------------------------------------
    // Core character handling
    // ---------------------------------------------------------------
    private void handleChar(List<Token> tokens, int index) {
        char c = tokens.get(index).ch;

        if (c == '\n') {
            newline();
            return;
        }

        // Word wrap: if the next word would overflow, break the line at this space
        if (c == ' ' && wrapWidth > 0 && column > 0
                && column + nextWordLength(tokens, index + 1) >= wrapWidth) {
            newline();
            return;
        }

        out.print(c);
        out.flush();
        column++;

        if (!skipping) {
            sleep(delayFor(c));
        }
        checkForSkip();
    }

    private void newline() {
        out.println();
        out.flush();
        column = 0;
    }

    /** Counts visible characters of the upcoming word (until space/newline). */
    private int nextWordLength(List<Token> tokens, int start) {
        int len = 0;
        for (int i = start; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type != TokenType.CHAR) continue;   // styles/pauses are zero-width
            if (t.ch == ' ' || t.ch == '\n') break;
            len++;
        }
        return len;
    }

    // ---------------------------------------------------------------
    // Timing
    // ---------------------------------------------------------------
    private long delayFor(char c) {
        if (speed == Speed.INSTANT) return 0;

        long base = speed.msPerChar;

        // Random jitter of +/-25% so it feels less robotic
        long jitter = (long) (base * 0.25 * (random.nextDouble() * 2 - 1));
        long delay = base + jitter;

        switch (c) {
            case '.':
            case '!':
            case '?':
                delay += base * 8;
                break;
            case ',':
            case ';':
            case ':':
                delay += base * 4;
                break;
            default:
                break;
        }
        return Math.max(0, delay);
    }

    private void sleep(long ms) {
        if (ms <= 0 || speed == Speed.INSTANT) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------------------------------------------------------
    // Skip-on-Enter
    // ---------------------------------------------------------------
    /**
     * Non-blocking check for Enter while typing. Most terminals only deliver
     * input after Enter is pressed, so this means "press Enter to skip".
     */
    private void checkForSkip() {
        if (!skipEnabled || skipping) return;
        try {
            while (in.available() > 0) {
                int b = in.read();
                if (b == '\n' || b == '\r') {
                    skipping = true;
                }
            }
        } catch (IOException e) {
            // ignore; skipping just won't work
        }
    }

    // ---------------------------------------------------------------
    // Parsing
    // ---------------------------------------------------------------
    private List<Token> parse(String text) {
        List<Token> tokens = new ArrayList<>();
        Matcher m = MARKUP.matcher(text);
        int last = 0;

        while (m.find()) {
            addChars(tokens, text.substring(last, m.start()));
            last = m.end();

            String whole = m.group();

            // Raw ANSI code from AnsiColors: zero-width, no delay
            if (whole.charAt(0) == '\u001B') {
                tokens.add(Token.ofStyle(whole));
                continue;
            }

            String name = m.group(1).toLowerCase();
            String number = m.group(2);

            if (name.equals("pause") && number != null) {
                tokens.add(Token.ofPause(Long.parseLong(number)));
            } else if (STYLES.containsKey(name)) {
                tokens.add(Token.ofStyle(STYLES.get(name)));
            } else {
                // Unknown tag: print it literally
                addChars(tokens, whole);
            }
        }
        addChars(tokens, text.substring(last));
        return tokens;
    }

    private void addChars(List<Token> tokens, String s) {
        for (int i = 0; i < s.length(); i++) {
            tokens.add(Token.ofChar(s.charAt(i)));
        }
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) sb.append(c);
        return sb.toString();
    }
}