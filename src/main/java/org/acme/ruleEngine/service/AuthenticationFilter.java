package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import org.acme.records.Authentication;
import org.acme.records.ServiceResponse;
import org.acme.utility.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    private final AuthenticationService authenticationService;
    private final UriInfo uriInfo;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Inject
    public AuthenticationFilter(AuthenticationService authenticationService, UriInfo uriInfo) {
        this.authenticationService = authenticationService;
        this.uriInfo = uriInfo;
    }

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        String path = uriInfo.getPath();

        if (path.equals("/auth/request") || path.equals("/user/create-client")) {
            return;
        }

        String authorization = crc.getHeaderString("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            ServiceResponse response = authenticationService.verifyToken(authorization);
            logger.info("Authentication Details ----->  {}", response);

            if (response.isSuccess()) {
                Authentication auth = authenticationService.getAuthenticationFromToken(authorization);
                List<String> roles = auth.roles();

                // Superadmin-only path
                if (path.equals("/user/create-superadmin")) {
                    if (roles.stream().noneMatch(role -> role.equalsIgnoreCase("super_admin"))) {
                        denyAccess(crc, "you do not have permission to perform this operation.");
                        return;
                    }
                }

                // Superadmin and admin allowed path
                if (path.equals("/user/create-admin")) {
                    if (roles.stream().noneMatch(role ->
                            role.equalsIgnoreCase("super_admin") || role.equalsIgnoreCase("admin"))) {
                        denyAccess(crc, "only super_admin and admin roles are allowed for this operation.");
                        return;
                    }
                }

                // Token valid and role permitted
                return;
            }
        }

        // Token missing or invalid
        var errorResponse = new ServiceResponse(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                false,
                "Access token is invalid or missing. Please ensure you have provided a valid access token."
        );
        crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(Util.buildResponse(errorResponse)).build());
    }

    private void denyAccess(ContainerRequestContext crc, String message) {
        var accessDenied = new ServiceResponse(
                Response.Status.FORBIDDEN.getStatusCode(),
                false,
                message
        );
        var error = Util.buildResponse(accessDenied);
        crc.abortWith(Response.status(Response.Status.FORBIDDEN).entity(error).build());
    }
}
