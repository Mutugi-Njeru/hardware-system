package org.acme.models;

import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;

public record Account(@NotBlank(message = "businessName cannot be blank") String businessName,

                      @NotBlank(message = "registrationPin cannot be blank") String registrationPin,

                      @NotBlank(message = "kraPin cannot be blank") String kraPin) {
    public Account(JsonObject object) {
        this(

                object.getString("businessName"), object.getString("registrationPin"), object.getString("kraPin"));
    }

}
