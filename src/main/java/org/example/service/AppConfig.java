package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final String FILE_NAME = "config.json";

    private String exportDir = System.getProperty("user.home") + "/Downloads/";
    private Integer deleteDoneAfterDays = 0;

    public void changeExportDir(String exportDir) {
        if (exportDir == null || exportDir.isBlank()) {
            this.exportDir = System.getProperty("user.home") + "/Downloads/";
            logger.warn("Export dir reset to default: {}", this.exportDir);
        } else {
            this.exportDir = exportDir.endsWith("/") ? exportDir : exportDir + "/";
            logger.info("Export dir changed to: {}", this.exportDir);
        }
        save();
    }

    public void changeDeleteDoneAfterDays(Integer days) {
        if (days == null || days <= 0) {
            this.deleteDoneAfterDays = 0;
            logger.warn("Delete done after days set to default: {}", this.deleteDoneAfterDays);
        } else {
            this.deleteDoneAfterDays = days;
            logger.info("Delete done after days changed to: {}", this.deleteDoneAfterDays);
        }
        save();
    }

    public String getExportDir() {
        return exportDir;
    }

    public Integer getDeleteDoneAfterDays() {
        return deleteDoneAfterDays;
    }

    public static AppConfig load() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            logger.info("Config file not found, using defaults.");
            return new AppConfig();
        }

        try {
            AppConfig config = mapper.readValue(file, AppConfig.class);
            logger.info("Loaded config from file: {}", FILE_NAME);
            return config;
        } catch (IOException e) {
            logger.error("Failed to load config from file: {}, using defaults.", FILE_NAME, e);
            return new AppConfig();
        }
    }

    public void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), this);
            logger.info("Config saved to file: {}", FILE_NAME);
        } catch (IOException e) {
            logger.error("Failed to save config to file: {}", FILE_NAME, e);
        }
    }
}
