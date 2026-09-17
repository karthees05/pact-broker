package com.example.contract;

public final class ContractSettings {
    public static final String CONSUMER_NAME = "JsonPlaceholderConsumer";
    public static final String PROVIDER_NAME = "JsonPlaceholderApi";
    public static final String RESOURCE_PATH = "/posts";
    public static final int EXISTING_RESOURCE_ID = 1;
    public static final int CREATED_RESOURCE_ID = 101;

    private ContractSettings() {
    }

    public static String resourcePath(int id) {
        return RESOURCE_PATH + "/" + id;
    }
}
