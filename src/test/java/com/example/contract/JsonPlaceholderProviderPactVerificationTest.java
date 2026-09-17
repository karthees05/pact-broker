package com.example.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Provider(ContractSettings.PROVIDER_NAME)
@PactFolder("build/pacts")
class JsonPlaceholderProviderPactVerificationTest {
    private static MockJsonPlaceholderProvider provider;
    private static URL providerUrl;

    @BeforeAll
    static void startProvider() throws IOException {
        provider = MockJsonPlaceholderProvider.start();
        providerUrl = provider.url();
    }

    @AfterAll
    static void stopProvider() {
        if (provider != null) {
            provider.stop();
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

    @State("posts exist")
    void postsExist() {
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

    @State("post 1 exists and can be deleted")
    void postOneCanBeDeleted() {
    }
}
