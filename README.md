# pact-broker

Java/Gradle consumer-driven Pact contract tests using the public
[JSONPlaceholder](https://jsonplaceholder.typicode.com/) API shape.

The consumer tests generate contracts from a Pact mock server. Provider
verification runs against a local JSONPlaceholder-compatible test provider so
the suite is reliable locally and in GitHub Actions without depending on a live
third-party API.

The project covers these operations:

- `GET /posts/1`
- `POST /posts`
- `PUT /posts/1`
- `PATCH /posts/1`

## Requirements

- Java 21

## Run locally

The Gradle Wrapper is included, so Gradle does not need to be installed first.

Generate the consumer pact file:

```bash
./gradlew consumerPactTest
```

Verify the generated pact against JSONPlaceholder:

```bash
./gradlew providerPactTest
```

Run both through the standard verification task:

```bash
./gradlew test
```

## Dashboard

The test run generates a local dashboard at:

```text
build/reports/pact-dashboard/index.html
```

The dashboard shows:

- consumer and provider names
- Pact specification version
- generated Pact file name
- all covered operations
- consumer and provider test pass/fail results

In GitHub Actions, the same dashboard is uploaded as the `pact-dashboard`
artifact. Open the workflow run, download the artifact, and open `index.html`.

## Pact Broker publishing

The GitHub Actions workflow always runs the consumer and provider contract tests.
It publishes generated pacts to a Pact Broker only when both repository secrets
are configured:

- `PACT_BROKER_BASE_URL`
- `PACT_BROKER_TOKEN`

You can publish locally with:

```bash
PACT_BROKER_BASE_URL=https://your-broker.example.com \
PACT_BROKER_TOKEN=your-token \
GITHUB_SHA=local \
GITHUB_REF_NAME=local \
./gradlew pactPublish
```

The generated pact is written to `build/pacts/`, which is ignored by git because
CI can regenerate it.
