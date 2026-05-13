package com.example.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(pactVersion = PactSpecVersion.V3)
class JsonPlaceholderConsumerPactTest {
    private static final String CONSUMER = "JsonPlaceholderConsumer";
    private static final String PROVIDER = "JsonPlaceholderApi";

    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public RequestResponsePact getPost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists")
            .uponReceiving("a request to get post 1")
            .path("/posts/1")
            .method("GET")
            .headers(Map.of("Accept", "application/json"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(new PactDslJsonBody()
                .integerType("userId", 1)
                .numberValue("id", 1)
                .stringType("title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
                .stringType("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getPost")
    void getsPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());

        Map<String, Object> post = client.getPost(1);

        assertThat(post)
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsKeys("title", "body");
    }

    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public RequestResponsePact createPost(PactDslWithProvider builder) {
        return builder
            .given("posts can be created")
            .uponReceiving("a request to create a post")
            .path("/posts")
            .method("POST")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(new PactDslJsonBody()
                .integerType("userId", 1)
                .stringType("title", "contract testing with pact")
                .stringType("body", "consumer driven contracts for create operations"))
            .willRespondWith()
            .status(201)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(new PactDslJsonBody()
                .integerType("userId", 1)
                .integerType("id", 101)
                .stringType("title", "contract testing with pact")
                .stringType("body", "consumer driven contracts for create operations"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPost")
    void createsPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());
        PostRequest request = new PostRequest(null, 1, "contract testing with pact", "consumer driven contracts for create operations");

        Map<String, Object> post = client.createPost(request);

        assertThat(post)
            .containsEntry("userId", 1)
            .containsEntry("title", request.title())
            .containsEntry("body", request.body())
            .containsKey("id");
    }

    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public RequestResponsePact replacePost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists and can be replaced")
            .uponReceiving("a request to replace post 1")
            .path("/posts/1")
            .method("PUT")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(new PactDslJsonBody()
                .numberValue("id", 1)
                .integerType("userId", 1)
                .stringType("title", "fully replaced title")
                .stringType("body", "fully replaced body"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(new PactDslJsonBody()
                .numberValue("id", 1)
                .integerType("userId", 1)
                .stringType("title", "fully replaced title")
                .stringType("body", "fully replaced body"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "replacePost")
    void replacesPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());
        PostRequest request = new PostRequest(1, 1, "fully replaced title", "fully replaced body");

        Map<String, Object> post = client.replacePost(1, request);

        assertThat(post)
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsEntry("title", request.title())
            .containsEntry("body", request.body());
    }

    @Pact(consumer = CONSUMER, provider = PROVIDER)
    public RequestResponsePact patchPost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists and can be patched")
            .uponReceiving("a request to patch post 1")
            .path("/posts/1")
            .method("PATCH")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(new PactDslJsonBody()
                .stringType("title", "partially updated title"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(new PactDslJsonBody()
                .integerType("userId", 1)
                .numberValue("id", 1)
                .stringType("title", "partially updated title")
                .stringType("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "patchPost")
    void patchesPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());

        Map<String, Object> post = client.updatePost(1, Map.of("title", "partially updated title"));

        assertThat(post)
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsEntry("title", "partially updated title")
            .containsKey("body");
    }
}
