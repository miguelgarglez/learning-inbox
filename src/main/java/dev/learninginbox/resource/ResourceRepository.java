package dev.learninginbox.resource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceRepository {
    private final JdbcClient jdbc;

    public ResourceRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(LearningResource resource) {
        jdbc.sql("""
                INSERT INTO resources (id, title, url, reason, status, created_at)
                VALUES (:id, :title, :url, :reason, :status, :createdAt)
                """)
                .param("id", resource.id())
                .param("title", resource.title())
                .param("url", resource.url())
                .param("reason", resource.reason())
                .param("status", resource.status().name())
                .param("createdAt", resource.createdAt().atOffset(ZoneOffset.UTC))
                .update();
    }

    public Optional<LearningResource> findById(UUID id) {
        return jdbc.sql("""
                SELECT id, title, url, reason, status, created_at
                FROM resources
                WHERE id = :id
                """)
                .param("id", id)
                .query((rs, rowNum) -> new LearningResource(
                        rs.getObject("id", UUID.class),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getString("reason"),
                        LearningResource.Status.valueOf(rs.getString("status")),
                        rs.getObject("created_at", OffsetDateTime.class).toInstant()))
                .optional();
    }
}
