package org.example;

public class SystemPrinter {

    public static void info(String message) {
        System.out.println(ConsoleColor.CYAN.wrap(message));
    }

    public static void warn(String message) {
        System.out.println(ConsoleColor.YELLOW.wrap(message));
    }

    public static void success(String message) {
        System.out.println(ConsoleColor.GREEN.wrap(message));
    }

    public static void error(String message) {
        System.err.println(ConsoleColor.RED.wrap(message));
    }
}
