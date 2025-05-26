package org.acme.models;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record User(
        @NotNull(message = "accountId is required")
        Integer accountId,

        @NotBlank(message = "username cannot be blank")
        String username,

        @NotBlank(message = "password cannot be blank")
        String password,

        @NotBlank(message = "firstname cannot be blank")
        String firstname,

        @NotBlank(message = "lastname cannot be blank")
        String lastname,

        @NotBlank(message = "msisdn cannot be blank")
        @Size(min = 12, message = "msisdn must have 12 digits")
        String msisdn,

        @NotBlank(message = "email cannot be empty")
        String email
) {
        public User(JsonObject object) {
                this(
                        object.getInt("accountId"),
                        object.getString("username"),
                        object.getString("password"),
                        object.getString("firstname"),
                        object.getString("lastname"),
                        object.getString("msisdn"),
                        object.getString("email")
                );
        }
}
