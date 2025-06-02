package org.acme.models;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItem(
        @NotNull(message = "userId is required")
        Integer userId,
        @NotNull(message = "productId is required")
        Integer productId,
        @NotNull(message = "quantityAvailable cannot be null")
        @Min(value = 1, message = "quantityAvailable must be greater than 0")
        Integer quantity
) {
        public CartItem(JsonObject object) {
                this(
                        object.getInt("userId"),
                        object.getInt("productId"),
                        object.getInt("quantity")
                );
        }
}
