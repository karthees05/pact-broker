package com.example.contract;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

final class MockJsonPlaceholderProvider {
    private final HttpServer server;
    private final Map<Route, Response> routes = new LinkedHashMap<>();

    private MockJsonPlaceholderProvider(HttpServer server) {
        this.server = server;
    }

    static MockJsonPlaceholderProvider start() throws IOException {
        MockJsonPlaceholderProvider provider = new MockJsonPlaceholderProvider(HttpServer.create(new InetSocketAddress(0), 0));
        provider.registerDefaultRoutes();
        provider.server.createContext(ContractSettings.RESOURCE_PATH, provider::handle);
        provider.server.start();
        return provider;
    }

    URL url() throws IOException {
        return URI.create("http://localhost:" + server.getAddress().getPort()).toURL();
    }

    void stop() {
        server.stop(0);
    }

    private void registerDefaultRoutes() {
        add("GET", ContractSettings.RESOURCE_PATH, 200, """
            [
              {
                "userId": 1,
                "id": 1,
                "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum"
              },
              {
                "userId": 1,
                "id": 2,
                "title": "qui est esse",
                "body": "est rerum tempore vitae sequi sint nihil reprehenderit dolor beatae ea dolores neque"
              }
            ]
            """);
        add("GET", ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID), 200, """
            {
              "userId": 1,
              "id": 1,
              "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum"
            }
            """);
        add("POST", ContractSettings.RESOURCE_PATH, 201, """
            {
              "userId": 1,
              "id": 101,
              "title": "contract testing with pact",
              "body": "consumer driven contracts for create operations"
            }
            """);
        add("PUT", ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID), 200, """
            {
              "id": 1,
              "userId": 1,
              "title": "fully replaced title",
              "body": "fully replaced body"
            }
            """);
        add("PATCH", ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID), 200, """
            {
              "userId": 1,
              "id": 1,
              "title": "partially updated title",
              "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum"
            }
            """);
        add("DELETE", ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID), 200, "{}");
    }

    private void add(String method, String path, int statusCode, String body) {
        routes.put(new Route(method, path), new Response(statusCode, body));
    }

    private void handle(HttpExchange exchange) throws IOException {
        Response response = routes.get(new Route(exchange.getRequestMethod(), exchange.getRequestURI().getPath()));
        if (response == null) {
            writeJson(exchange, 404, """
                {
                  "error": "not found"
                }
                """);
            return;
        }

        writeJson(exchange, response.statusCode(), response.body());
    }

    private static void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }

    private record Route(String method, String path) {
    }

    private record Response(int statusCode, String body) {
    }
}
