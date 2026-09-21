package dev.learninginbox.resource;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import dev.learninginbox.security.DevApiKeys;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ResourceApiTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcClient jdbc;

    @BeforeEach
    void clearResources() {
        jdbc.sql("DELETE FROM resources").update();
    }

    @Test
    void savesAndRetrievesTheSameResource() throws Exception {
        var before = Instant.now();
        var created = post(DevApiKeys.ALICE, """
                {"title":"  Transactions  ","url":"https://example.com/article","reason":"Learn rollback"}
                """);
        assertThat(created.statusCode()).isEqualTo(201);
        Map<String, Object> resource = JsonPath.read(created.body(), "$");
        var id = UUID.fromString((String) resource.get("id"));
        var createdAt = Instant.parse((String) resource.get("createdAt"));
        assertThat(createdAt).isBetween(before, Instant.now());
        assertThat(resource).containsEntry("title", "Transactions")
                .containsEntry("url", "https://example.com/article")
                .containsEntry("reason", "Learn rollback")
                .containsEntry("status", "PENDING")
                .containsEntry("ownerId", DevApiKeys.ALICE_ID.toString());
        var location = created.headers().firstValue("Location").orElseThrow();
        assertThat(location).isEqualTo("/api/resources/" + id);
        var fetched = get(DevApiKeys.ALICE, location);
        assertThat(fetched.statusCode()).isEqualTo(200);
        Map<String, Object> fetchedResource = JsonPath.read(fetched.body(), "$");
        assertThat(fetchedResource).isEqualTo(resource);
    }

    @Test
    void storesResourceInPostgreSQLWithOwner() throws Exception {
        var created = post(DevApiKeys.ALICE, """
                {"title":"Persisted","url":"https://example.com/persisted","reason":"DB row"}
                """);
        assertThat(created.statusCode()).isEqualTo(201);
        var id = UUID.fromString(JsonPath.read(created.body(), "$.id"));
        var ownerId = jdbc.sql("SELECT owner_id FROM resources WHERE id = :id")
                .param("id", id)
                .query(UUID.class)
                .optional();
        assertThat(ownerId).contains(DevApiKeys.ALICE_ID);
    }

    @Test
    void assignsDifferentIdsAndAllowsAnOmittedReason() throws Exception {
        String body = """
                {"title":"Article","url":"https://example.com/article"}
                """;
        var first = post(DevApiKeys.ALICE, body);
        var second = post(DevApiKeys.ALICE, body);
        assertThat(first.statusCode()).isEqualTo(201);
        assertThat(second.statusCode()).isEqualTo(201);
        assertThat(first.headers().firstValue("Location"))
                .isNotEqualTo(second.headers().firstValue("Location"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "null", "{broken",
            "{\"title\":\"   \",\"url\":\"https://example.com\"}",
            "{\"title\":\"Article\",\"url\":\"relative/path\"}",
            "{\"title\":\"Article\",\"url\":\"ftp://example.com\"}",
            "{\"title\":\"Article\",\"url\":\"https://user:pass@example.com\"}",
            "{\"title\":\"Article\",\"url\":\"https://\"}"
    })
    void rejectsInvalidInput(String body) throws Exception {
        assertProblem(post(DevApiKeys.ALICE, body), 400);
        assertThat(jdbc.sql("SELECT count(*) FROM resources").query(Long.class).single()).isZero();
    }

    @Test
    void enforcesLengthLimitsAfterTrimmingTitle() throws Exception {
        assertThat(post(DevApiKeys.ALICE, body(" " + "x".repeat(200) + " ", "https://example.com", "x".repeat(1000)))
                .statusCode()).isEqualTo(201);
        assertProblem(post(DevApiKeys.ALICE, body("x".repeat(201), "https://example.com", "")), 400);
        assertProblem(post(DevApiKeys.ALICE, body("Article", "https://example.com", "x".repeat(1001))), 400);
        assertProblem(post(DevApiKeys.ALICE, body("Article", "https://example.com/" + "x".repeat(2048), "")), 400);
    }

    @Test
    void distinguishesMissingResourceFromMalformedId() throws Exception {
        assertProblem(get(DevApiKeys.ALICE, "/api/resources/" + UUID.randomUUID()), 404);
        assertProblem(get(DevApiKeys.ALICE, "/api/resources/not-a-uuid"), 400);
    }

    @Test
    void rejectsMissingOrInvalidApiKey() throws Exception {
        assertProblem(send(HttpRequest.newBuilder(endpoint("/api/resources"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"title":"No auth","url":"https://example.com/no-auth"}
                        """))), 401);
        assertProblem(post("li_not_a_real_key", """
                {"title":"Bad key","url":"https://example.com/bad-key"}
                """), 401);
    }

    @Test
    void hidesAnotherUsersResource() throws Exception {
        var created = post(DevApiKeys.ALICE, """
                {"title":"Alice only","url":"https://example.com/alice"}
                """);
        assertThat(created.statusCode()).isEqualTo(201);
        var location = created.headers().firstValue("Location").orElseThrow();
        assertProblem(get(DevApiKeys.BOB, location), 404);
        assertThat(get(DevApiKeys.ALICE, location).statusCode()).isEqualTo(200);
    }

    private String body(String title, String url, String reason) {
        return "{\"title\":\"%s\",\"url\":\"%s\",\"reason\":\"%s\"}".formatted(title, url, reason);
    }

    private void assertProblem(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(response.headers().firstValue("Content-Type").orElseThrow())
                .startsWith("application/problem+json");
        int reportedStatus = JsonPath.read(response.body(), "$.status");
        assertThat(reportedStatus).isEqualTo(status);
    }

    private HttpResponse<String> post(String apiKey, String body) throws Exception {
        return send(HttpRequest.newBuilder(endpoint("/api/resources"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body)));
    }

    private HttpResponse<String> get(String apiKey, String path) throws Exception {
        return send(HttpRequest.newBuilder(endpoint(path))
                .header("Authorization", "Bearer " + apiKey)
                .GET());
    }

    private URI endpoint(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private HttpResponse<String> send(HttpRequest.Builder request) throws Exception {
        try (var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            return client.send(request.timeout(Duration.ofSeconds(5)).build(), HttpResponse.BodyHandlers.ofString());
        }
    }
}
