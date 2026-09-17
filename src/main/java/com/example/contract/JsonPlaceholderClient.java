package com.example.contract;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class JsonPlaceholderClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient;
    private final URI baseUri;

    public JsonPlaceholderClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUri = URI.create(stripTrailingSlash(baseUrl));
    }

    public List<Map<String, Object>> listPosts() {
        return sendList(HttpRequest.newBuilder(baseUri.resolve(ContractSettings.RESOURCE_PATH))
            .GET()
            .header("Accept", "application/json")
            .build());
    }

    public Map<String, Object> getPost(int id) {
        return sendMap(HttpRequest.newBuilder(baseUri.resolve(ContractSettings.resourcePath(id)))
            .GET()
            .header("Accept", "application/json")
            .build());
    }

    public Map<String, Object> createPost(PostRequest post) {
        return sendMap(jsonRequest(ContractSettings.RESOURCE_PATH, "POST", post));
    }

    public Map<String, Object> replacePost(int id, PostRequest post) {
        return sendMap(jsonRequest(ContractSettings.resourcePath(id), "PUT", post));
    }

    public Map<String, Object> updatePost(int id, Map<String, Object> patch) {
        return sendMap(jsonRequest(ContractSettings.resourcePath(id), "PATCH", patch));
    }

    public Map<String, Object> deletePost(int id) {
        return sendMap(HttpRequest.newBuilder(baseUri.resolve(ContractSettings.resourcePath(id)))
            .DELETE()
            .header("Accept", "application/json")
            .build());
    }

    private HttpRequest jsonRequest(String path, String method, Object body) {
        try {
            return HttpRequest.newBuilder(baseUri.resolve(path))
                .method(method, HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to serialize request body", e);
        }
    }

    private Map<String, Object> sendMap(HttpRequest request) {
        String body = send(request);
        try {
            return OBJECT_MAPPER.readValue(body, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse JSONPlaceholder response", e);
        }
    }

    private List<Map<String, Object>> sendList(HttpRequest request) {
        String body = send(request);
        try {
            return OBJECT_MAPPER.readValue(body, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse JSONPlaceholder response", e);
        }
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() > 299) {
                throw new IllegalStateException("JSONPlaceholder request failed with status " + response.statusCode());
            }

            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to call JSONPlaceholder", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling JSONPlaceholder", e);
        }
    }

    private static String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
