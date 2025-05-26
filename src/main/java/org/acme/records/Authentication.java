package org.acme.records;

import java.util.List;

public record Authentication(
        boolean isAuthenticated,
        String username,
        List<String> roles
) {
}