package dev.learninginbox.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ApiKeyRepository {
    private final JdbcClient jdbc;

    public ApiKeyRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<AuthenticatedUser> findUserByToken(String token) {
        return jdbc.sql("""
                SELECT u.id, u.username
                FROM api_keys k
                JOIN users u ON u.id = k.user_id
                WHERE k.token = :token
                """)
                .param("token", token)
                .query((rs, rowNum) -> new AuthenticatedUser(
                        rs.getObject("id", UUID.class),
                        rs.getString("username")))
                .optional();
    }
}
