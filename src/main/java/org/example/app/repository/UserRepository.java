package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.app.domain.UserWithRole;
import org.example.app.dto.PasswordResetConfirmRequestDto;
import org.example.app.dto.PasswordResetConfirmResponseDto;
import org.example.app.dto.PasswordResetRequestDto;
import org.example.app.dto.PasswordResetResponseDto;
import org.example.app.entity.UserEntity;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = resultSet -> new User(
            resultSet.getLong("id"),
            resultSet.getString("username")
    );
    private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password")
    );

    private final RowMapper<UserWithRole> rowMapperWithRole = resultSet -> new UserWithRole(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("role")
    );
    private final RowMapper<PasswordResetConfirmRequestDto> rowMapperForPasswordResetCode = resultSet -> new PasswordResetConfirmRequestDto(
            resultSet.getString("code"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getBoolean("active")
    );

    public Optional<User> getByUsername(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
    }

    public Optional<UserWithPassword> getByUsernameWithPassword(EntityManager entityManager, EntityTransaction transaction, String username) {
        // em, emt - closeable
        return entityManager.createNamedQuery(UserEntity.FIND_BY_USERNAME, UserEntity.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultStream()
                .map(o -> new UserWithPassword(o.getId(), o.getUsername(), o.getPassword()))
                .findFirst();
        // language=PostgreSQL
        // return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
    }

    public Optional<UserWithPassword> getByUsernameAndPassword(String username, String password) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT id, username, password FROM users 
                        WHERE username = ?
                        """,
                rowMapperWithPassword,
                username);
    }

    /**
     * saves user to db
     *
     * @param id       - user id, if 0 - insert, if not 0 - update
     * @param username
     * @param hash
     */
    // TODO: DuplicateKeyException <-
    public Optional<User> save(long id, String username, String hash) {
        // language=PostgreSQL
        return id == 0 ? jdbcTemplate.queryOne(
                """
                        INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username
                        """,
                rowMapper,
                username, hash
        ) : jdbcTemplate.queryOne(
                """
                        UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
                        """,
                rowMapper,
                username, hash, id
        );
    }

    public Optional<User> findByToken(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE t.token = ?
                        """,
                rowMapper,
                token
        );
    }

    public Optional<UserWithRole> findByTokenWithRole(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username, u.role FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE t.token = ?
                        """,
                rowMapperWithRole,
                token
        );
    }

    public void saveToken(long userId, String token) {
        // query - SELECT'ов (ResultSet)
        // update - ? int/long
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO tokens(token, "userId") VALUES (?, ?)
                        """,
                token, userId
        );
    }

    public String generateRandomCode() {
        for (; ; ) {
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100500, 1000000));
            if (fingByResetCode(code).isEmpty()) {
                return code;
            }
        }
    }

    public int resetPassword(String username, String passwordEncoded) {
        String code = generateRandomCode();
        // language=PostgreSQL
         return jdbcTemplate.update(
                """
                        INSERT INTO reset_codes(code, username, password) VALUES (?, ?, ?)
                        """,
                code,
                username,
                passwordEncoded
        );
    }

    public int resetPasswordConfirm(String username, String code) {
        final var resetCodeInDB = fingByResetCode(code);
        // language=PostgreSQL
         return jdbcTemplate.update(
                """
                        UPDATE users SET password = ? WHERE username = ?;
                        UPDATE reset_codes SET active = ? WHERE username = ?;
                        """,
                resetCodeInDB.get().getPassword(), username,
                true, username
        );
    }

    //TODO ***
    public void saveResetCode() {

    }

    public Optional<PasswordResetConfirmRequestDto> fingByResetCode(String code) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT code, username, password, active FROM reset_codes where code = ?
                        """,
                rowMapperForPasswordResetCode,
                code
        );
    }

    public Optional<User> findCardOwnerByCardId(long id) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username FROM users u
                        Join cards c on u.id = c."ownerId"
                        WHERE c.id = ?""",
                rowMapper,
                id
        );
    }


    public Optional<User> findCardOwnerByCardNumber(String cardNumber) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username FROM users u
                        Join cards c on u.id = c."ownerId"
                        WHERE c.number = ?
                        """,
                rowMapper,
                cardNumber
        );
    }
}
