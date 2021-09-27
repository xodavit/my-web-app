package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bouncycastle.util.encoders.Base64;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class BasicAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;

    private String username = "";
    private String password = "";
    private String realm = "Protected";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.BASIC_PROVIDER_ATTR));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }

        final var authHeader = req.getHeader("Authorization");
        if (authHeader == null) {
            unauthorized(res,"Header is null");
            super.doFilter(req, res, chain);
            return;
        } else {

            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                try {
                    String credentials = new String(Base64.decode(st.nextToken()), StandardCharsets.UTF_8);
                    try {
                        final var authentication = provider.authenticate(new BasicAuthentication("Basic", credentials));
                        req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
                    } catch (AuthenticationException e) {
                        unauthorized(res, "Invalid authentication");
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    unauthorized(res, "UnsupportedEncodingException");
                    return;
                }
            }

            super.doFilter(req, res, chain);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private boolean authenticationIsRequired(HttpServletRequest req) {
        final var existingAuth = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return AnonymousAuthentication.class.isAssignableFrom(existingAuth.getClass());
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        response.sendError(401, message);
    }
    private void unauthorized(HttpServletResponse response) throws IOException {
        unauthorized(response, "Unauthorized");
    }
}
