package hu.bsstudio.raktr.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static hu.bsstudio.raktr.security.SecurityConstants.EXPIRATION_TIME;
import static hu.bsstudio.raktr.security.SecurityConstants.HEADER_STRING;
import static hu.bsstudio.raktr.security.SecurityConstants.SECRET;
import static hu.bsstudio.raktr.security.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bsstudio.raktr.model.UserLoginData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req,
                                                final HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginData creds = new ObjectMapper()
                .readValue(req.getInputStream(), UserLoginData.class);

            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    creds.getUsername(),
                    creds.getPassword(),
                    new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    protected void successfulAuthentication(final HttpServletRequest req,
                                            final HttpServletResponse res,
                                            final FilterChain chain,
                                            final Authentication auth) throws IOException, ServletException {

        String token = JWT.create()
            .withSubject(((LdapUser) auth.getPrincipal()).getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
