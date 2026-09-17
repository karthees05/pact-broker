package com.example.contract;

import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

import java.util.Map;

final class ContractTestFixtures {
    static final PostRequest CREATE_POST = new PostRequest(
        null,
        1,
        "contract testing with pact",
        "consumer driven contracts for create operations");

    static final PostRequest REPLACE_POST = new PostRequest(
        ContractSettings.EXISTING_RESOURCE_ID,
        1,
        "fully replaced title",
        "fully replaced body");

    static final Map<String, Object> PATCH_POST = Map.of("title", "partially updated title");

    private ContractTestFixtures() {
    }

    static PactDslJsonArray postListBody() {
        PactDslJsonArray posts = new PactDslJsonArray();
        posts.object()
            .integerType("userId", 1)
            .numberValue("id", ContractSettings.EXISTING_RESOURCE_ID)
            .stringType("title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
            .stringType("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum")
            .closeObject();
        posts.object()
            .integerType("userId", 1)
            .numberValue("id", 2)
            .stringType("title", "qui est esse")
            .stringType("body", "est rerum tempore vitae sequi sint nihil reprehenderit dolor beatae ea dolores neque")
            .closeObject();
        return posts;
    }

    static PactDslJsonBody existingPostBody() {
        return new PactDslJsonBody()
            .integerType("userId", 1)
            .numberValue("id", ContractSettings.EXISTING_RESOURCE_ID)
            .stringType("title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
            .stringType("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum");
    }

    static PactDslJsonBody createRequestBody() {
        return new PactDslJsonBody()
            .integerType("userId", CREATE_POST.userId())
            .stringType("title", CREATE_POST.title())
            .stringType("body", CREATE_POST.body());
    }

    static PactDslJsonBody createResponseBody() {
        return new PactDslJsonBody()
            .integerType("userId", CREATE_POST.userId())
            .integerType("id", ContractSettings.CREATED_RESOURCE_ID)
            .stringType("title", CREATE_POST.title())
            .stringType("body", CREATE_POST.body());
    }

    static PactDslJsonBody replaceBody() {
        return new PactDslJsonBody()
            .numberValue("id", REPLACE_POST.id())
            .integerType("userId", REPLACE_POST.userId())
            .stringType("title", REPLACE_POST.title())
            .stringType("body", REPLACE_POST.body());
    }

    static PactDslJsonBody patchRequestBody() {
        return new PactDslJsonBody()
            .stringType("title", PATCH_POST.get("title").toString());
    }

    static PactDslJsonBody patchResponseBody() {
        return new PactDslJsonBody()
            .integerType("userId", 1)
            .numberValue("id", ContractSettings.EXISTING_RESOURCE_ID)
            .stringType("title", PATCH_POST.get("title").toString())
            .stringType("body", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum");
    }
}
