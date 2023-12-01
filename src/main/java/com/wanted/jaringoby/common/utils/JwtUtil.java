package com.wanted.jaringoby.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wanted.jaringoby.common.exceptions.jwt.TokenDecodingFailedException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenSignatureInvalidException;
import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtUtil {

    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_CUSTOMER_ID = "customerId";

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private final Algorithm algorithm;
    private final Long VALID_TIME_ACCESS_TOKEN;
    private final Long VALID_TIME_REFRESH_TOKEN;

    public String issueAccessToken(CustomerId customerId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + VALID_TIME_ACCESS_TOKEN);

        return JWT.create()
                .withClaim(CLAIM_TYPE, ACCESS_TOKEN)
                .withClaim(CLAIM_CUSTOMER_ID, customerId.value())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public String issueRefreshToken(CustomerId customerId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + VALID_TIME_REFRESH_TOKEN);

        return JWT.create()
                .withClaim(CLAIM_TYPE, REFRESH_TOKEN)
                .withClaim(CLAIM_CUSTOMER_ID, customerId.value())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public Map<String, String> decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        try {
            DecodedJWT decoded = verifier.verify(token);

            Map<String, Claim> claims = decoded.getClaims();

            return claims.entrySet()
                    .stream()
                    .filter(claim -> {
                        String name = claim.getKey();
                        return name.equals(CLAIM_TYPE) || name.equals(CLAIM_CUSTOMER_ID);
                    })
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            claim -> claim.getValue().asString()
                    ));

        } catch (SignatureVerificationException exception) {
            throw new TokenSignatureInvalidException();

        } catch (TokenExpiredException exception) {
            throw new com.wanted.jaringoby.common.exceptions.jwt
                    .TokenExpiredException();

        } catch (JWTDecodeException exception) {
            throw new TokenDecodingFailedException();
        }
    }
}
