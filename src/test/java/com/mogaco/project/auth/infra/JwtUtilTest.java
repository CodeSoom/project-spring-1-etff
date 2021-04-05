package com.mogaco.project.auth.infra;

import com.mogaco.project.auth.application.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789010";
    private static final Long GIVEN_ID = 1L;
    private static final long EXPIRED_TIME = 300_000;
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY0MDk2MjgwMCwiZXh" +
            "wIjoxNjQwOTYzMTAwfQ.2siRnBJmRU2JXjZY0CkQMgnCHRJN4Dld4_wG6R7T-HQ";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRED_TIME);
    }

    @Nested
    @DisplayName("encode 메서드는 ")
    class Describe_encode {

        @Nested
        @DisplayName("사용자 id가 주어지면")
        class Context_with_user_id {
            final Long userid = GIVEN_ID;

            @DisplayName("사용자 토큰을 리턴한다.")
            @Test
            void It_returns_token() {
                String token = jwtUtil.createToken(userid);

                assertThat(token).contains(".");
            }
        }
    }

    @Nested
    @DisplayName("decode 메서드는 ")
    class Describe_decode {

        @Nested
        @DisplayName("토큰이 주어지면")
        class Context_with_token {
            final String token = VALID_TOKEN;

            @DisplayName("토큰을 해독해 사용자 정보를 리턴한다.")
            @Test
            void It_returns_claim() {
                Claims claims = jwtUtil.decode(token);

                assertThat(claims.get("userId", Long.class)).isEqualTo(GIVEN_ID);
                assertThat(claims.getIssuedAt()).isNotNull();
                assertThat(claims.getExpiration()).isNotNull();
            }
        }

        @Nested
        @DisplayName("비어있는 토큰이 주어지면")
        class Context_with_empty_token {

            @DisplayName("예외를 던진다.")
            @ParameterizedTest
            @NullAndEmptySource
            void It_throws_exception(String token) {
                assertThrows(InvalidTokenException.class,
                        () -> jwtUtil.decode(token));
            }
        }
    }

    @Nested
    @DisplayName("유효하지 않은 토큰이 주어지면")
    class Context_with_invalid_token {

        @DisplayName("예외를 던진다.")
        @Test
        void It_throws_exception() {
            assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.decode(INVALID_TOKEN));
        }
    }

    @Nested
    @DisplayName("validate 메서드는 ")
    class Describe_validate {

        @Nested
        @DisplayName("유효한 토큰이 주어지면")
        class Context_with_valid_token {

            @DisplayName("true를 반환한다.")
            @Test
            void It_returns_false() {
                assertThat(jwtUtil.validateToken(VALID_TOKEN)).isTrue();
            }
        }
    }
}
