package org.acme.models;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.*;

public record Product(
        @NotNull(message = "accountId is required")
        Integer accountId,

        @NotNull(message = "unitId is required")
        Integer unitId,

        @NotBlank(message = "product name cannot be blank")
        String name,

        @NotNull(message = "pricePerUnit cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "pricePerUnit must be greater than 0")
        Double pricePerUnit,

        @NotNull(message = "quantityAvailable cannot be null")
        @Min(value = 1, message = "quantityAvailable must be greater than 0")
        Integer quantityAvailable,

        @NotBlank(message = "imageUrlOne cannot be blank")
        String imageUrlOne,

        @NotBlank(message = "imageUrlTwo cannot be empty")
        String imageUrlTwo
) {
    public Product(JsonObject object) {
        this(
                object.getInt("accountId"),
                object.getInt("unitId"),
                object.getString("name"),
                object.getJsonNumber("pricePerUnit").doubleValue(),
                object.getInt("quantityAvailable"),
                object.getString("imageUrlOne"),
                object.getString("imageUrlTwo")
        );
    }
}

