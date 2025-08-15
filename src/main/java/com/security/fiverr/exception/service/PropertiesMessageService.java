package com.security.fiverr.exception.service;

public interface PropertiesMessageService {
    String getProperty(String source);

    String getProperty(String name, Object... params);
}