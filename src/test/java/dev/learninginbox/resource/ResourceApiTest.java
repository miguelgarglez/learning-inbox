package dev.learninginbox.resource;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
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
        var created = post("""
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
                .containsEntry("status", "PENDING");
        var location = created.headers().firstValue("Location").orElseThrow();
        assertThat(location).isEqualTo("/api/resources/" + id);
        var fetched = get(location);
        assertThat(fetched.statusCode()).isEqualTo(200);
        Map<String, Object> fetchedResource = JsonPath.read(fetched.body(), "$");
        assertThat(fetchedResource).isEqualTo(resource);
    }

    @Test
    void storesResourceInPostgreSQL() throws Exception {
        var created = post("""
                {"title":"Persisted","url":"https://example.com/persisted","reason":"DB row"}
                """);
        assertThat(created.statusCode()).isEqualTo(201);
        var id = UUID.fromString(JsonPath.read(created.body(), "$.id"));
        var title = jdbc.sql("SELECT title FROM resources WHERE id = :id")
                .param("id", id)
                .query(String.class)
                .optional();
        assertThat(title).contains("Persisted");
    }

    @Test
    void assignsDifferentIdsAndAllowsAnOmittedReason() throws Exception {
        String body = """
                {"title":"Article","url":"https://example.com/article"}
                """;
        var first = post(body);
        var second = post(body);
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
        assertProblem(post(body), 400);
        assertThat(jdbc.sql("SELECT count(*) FROM resources").query(Long.class).single()).isZero();
    }

    @Test
    void enforcesLengthLimitsAfterTrimmingTitle() throws Exception {
        assertThat(post(body(" " + "x".repeat(200) + " ", "https://example.com", "x".repeat(1000)))
                .statusCode()).isEqualTo(201);
        assertProblem(post(body("x".repeat(201), "https://example.com", "")), 400);
        assertProblem(post(body("Article", "https://example.com", "x".repeat(1001))), 400);
        assertProblem(post(body("Article", "https://example.com/" + "x".repeat(2048), "")), 400);
    }

    @Test
    void distinguishesMissingResourceFromMalformedId() throws Exception {
        assertProblem(get("/api/resources/" + UUID.randomUUID()), 404);
        assertProblem(get("/api/resources/not-a-uuid"), 400);
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

    private HttpResponse<String> post(String body) throws Exception {
        return send(HttpRequest.newBuilder(endpoint("/api/resources"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return send(HttpRequest.newBuilder(endpoint(path)).GET());
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
