package dev.learninginbox.resource;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResourceDurabilityTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @LocalServerPort
    private int port;

    private static String location;
    private static String createdBody;

    @Test
    @Order(1)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createsResourceInPostgreSQL() throws Exception {
        var created = send(HttpRequest.newBuilder(endpoint("/api/resources"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"title":"Survives reload","url":"https://example.com/reload"}
                        """)));
        assertThat(created.statusCode()).isEqualTo(201);
        location = created.headers().firstValue("Location").orElseThrow();
        createdBody = created.body();
    }

    @Test
    @Order(2)
    void retrievesResourceAfterSpringContextReload() throws Exception {
        var fetched = send(HttpRequest.newBuilder(endpoint(location)).GET());
        assertThat(fetched.statusCode()).isEqualTo(200);
        assertThat((String) JsonPath.read(fetched.body(), "$.title")).isEqualTo("Survives reload");
        assertThat((String) JsonPath.read(fetched.body(), "$.id"))
                .isEqualTo(JsonPath.read(createdBody, "$.id"));
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
