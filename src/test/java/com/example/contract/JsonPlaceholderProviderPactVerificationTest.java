package com.example.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Provider("JsonPlaceholderApi")
@PactFolder("build/pacts")
class JsonPlaceholderProviderPactVerificationTest {
    private static HttpServer server;
    private static URL providerUrl;

    @BeforeAll
    static void startProvider() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/posts", JsonPlaceholderProviderPactVerificationTest::handlePosts);
        server.start();
        providerUrl = new URL("http", "localhost", server.getAddress().getPort(), "");
    }

    @AfterAll
    static void stopProvider() {
        if (server != null) {
            server.stop(0);
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) throws MalformedURLException {
        context.setTarget(HttpTestTarget.fromUrl(providerUrl));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("post 1 exists")
    void postOneExists() {
    }

    @State("posts can be created")
    void postsCanBeCreated() {
    }

    @State("post 1 exists and can be replaced")
    void postOneCanBeReplaced() {
    }

    @State("post 1 exists and can be patched")
    void postOneCanBePatched() {
    }

    private static void handlePosts(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/posts/1".equals(path) && "GET".equals(method)) {
            writeJson(exchange, 200, """
                {
                  "userId": 1,
                  "id": 1,
                  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                  "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum"
                }
                """);
            return;
        }

        if ("/posts".equals(path) && "POST".equals(method)) {
            writeJson(exchange, 201, """
                {
                  "userId": 1,
                  "id": 101,
                  "title": "contract testing with pact",
                  "body": "consumer driven contracts for create operations"
                }
                """);
            return;
        }

        if ("/posts/1".equals(path) && "PUT".equals(method)) {
            writeJson(exchange, 200, """
                {
                  "id": 1,
                  "userId": 1,
                  "title": "fully replaced title",
                  "body": "fully replaced body"
                }
                """);
            return;
        }

        if ("/posts/1".equals(path) && "PATCH".equals(method)) {
            writeJson(exchange, 200, """
                {
                  "userId": 1,
                  "id": 1,
                  "title": "partially updated title",
                  "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum"
                }
                """);
            return;
        }

        writeJson(exchange, 404, """
            {
              "error": "not found"
            }
            """);
    }

    private static void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }
}
