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
    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact listPosts(PactDslWithProvider builder) {
        return builder
            .given("posts exist")
            .uponReceiving("a request to list posts")
            .path(ContractSettings.RESOURCE_PATH)
            .method("GET")
            .headers(Map.of("Accept", "application/json"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(ContractTestFixtures.postListBody())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "listPosts")
    void listsPosts(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());

        var posts = client.listPosts();

        assertThat(posts).hasSizeGreaterThanOrEqualTo(2);
        assertThat(posts.getFirst())
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsKeys("title", "body");
    }

    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact getPost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists")
            .uponReceiving("a request to get post 1")
            .path(ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID))
            .method("GET")
            .headers(Map.of("Accept", "application/json"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(ContractTestFixtures.existingPostBody())
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

    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact createPost(PactDslWithProvider builder) {
        return builder
            .given("posts can be created")
            .uponReceiving("a request to create a post")
            .path(ContractSettings.RESOURCE_PATH)
            .method("POST")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(ContractTestFixtures.createRequestBody())
            .willRespondWith()
            .status(201)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(ContractTestFixtures.createResponseBody())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPost")
    void createsPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());
        PostRequest request = ContractTestFixtures.CREATE_POST;

        Map<String, Object> post = client.createPost(request);

        assertThat(post)
            .containsEntry("userId", 1)
            .containsEntry("title", request.title())
            .containsEntry("body", request.body())
            .containsKey("id");
    }

    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact replacePost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists and can be replaced")
            .uponReceiving("a request to replace post 1")
            .path(ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID))
            .method("PUT")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(ContractTestFixtures.replaceBody())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(ContractTestFixtures.replaceBody())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "replacePost")
    void replacesPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());
        PostRequest request = ContractTestFixtures.REPLACE_POST;

        Map<String, Object> post = client.replacePost(1, request);

        assertThat(post)
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsEntry("title", request.title())
            .containsEntry("body", request.body());
    }

    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact patchPost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists and can be patched")
            .uponReceiving("a request to patch post 1")
            .path(ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID))
            .method("PATCH")
            .headers(Map.of(
                "Accept", "application/json",
                "Content-Type", "application/json; charset=UTF-8"))
            .body(ContractTestFixtures.patchRequestBody())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(ContractTestFixtures.patchResponseBody())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "patchPost")
    void patchesPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());

        Map<String, Object> post = client.updatePost(ContractSettings.EXISTING_RESOURCE_ID, ContractTestFixtures.PATCH_POST);

        assertThat(post)
            .containsEntry("id", 1)
            .containsEntry("userId", 1)
            .containsEntry("title", "partially updated title")
            .containsKey("body");
    }

    @Pact(consumer = ContractSettings.CONSUMER_NAME, provider = ContractSettings.PROVIDER_NAME)
    public RequestResponsePact deletePost(PactDslWithProvider builder) {
        return builder
            .given("post 1 exists and can be deleted")
            .uponReceiving("a request to delete post 1")
            .path(ContractSettings.resourcePath(ContractSettings.EXISTING_RESOURCE_ID))
            .method("DELETE")
            .headers(Map.of("Accept", "application/json"))
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
            .body(new PactDslJsonBody())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "deletePost")
    void deletesPost(MockServer mockServer) {
        JsonPlaceholderClient client = new JsonPlaceholderClient(mockServer.getUrl());

        Map<String, Object> response = client.deletePost(ContractSettings.EXISTING_RESOURCE_ID);

        assertThat(response).isEmpty();
    }
}
