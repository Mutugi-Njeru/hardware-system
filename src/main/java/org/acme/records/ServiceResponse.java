package org.acme.records;

public record ServiceResponse(
        int statusCode, boolean isSuccess, Object message
) {
}
