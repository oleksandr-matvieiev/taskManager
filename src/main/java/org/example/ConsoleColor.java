package org.example;

public enum ConsoleColor {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m");

    private final String code;

    ConsoleColor(String code) {
        this.code = code;
    }

    public String wrap(String text) {
        return code + text + RESET.code;
    }
}
