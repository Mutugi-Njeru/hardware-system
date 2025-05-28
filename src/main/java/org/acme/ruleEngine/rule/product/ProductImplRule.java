package org.acme.ruleEngine.rule.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.acme.enums.Modules;
import org.acme.enums.RequestTypes;
import org.acme.models.Account;
import org.acme.models.Product;
import org.acme.ruleEngine.interfaces.ServiceRule;
import org.acme.ruleEngine.service.BeanValidatorService;
import org.acme.ruleEngine.service.ProductService;
import org.acme.utility.Util;

import java.util.List;

@ApplicationScoped
public class ProductImplRule implements ServiceRule {
    private final ProductService productService;
    private final BeanValidatorService validatorService;

    @Inject
    public ProductImplRule(ProductService productService, BeanValidatorService validatorService) {
        this.productService = productService;
        this.validatorService = validatorService;
    }

    @Override
    public boolean matches(Object module) {
        return (module.toString().equals(Modules.PRODUCT.name()));
    }

    @Override
    public Object apply(Object request) {
        JsonObject requestBody = Util.convertObjectToJson(request);
        String requestType = requestBody.getString("requestType", "");

        switch (RequestTypes.valueOf(requestType)) {
            case CREATE_PRODUCT:
                Product product = new Product(requestBody);
                List<String> violations = validatorService.validateDTO(product);

                if (!violations.isEmpty()) {
                    return Json.createObjectBuilder().add("message", String.valueOf(violations)).build();
                }
                return Util.buildResponse(productService.createProduct(product));
            case GET_ALL_PRODUCTS:
                return Util.buildResponse(productService.getAllProducts(requestBody));
            default:
                throw new IllegalArgumentException("Unhandled request type: " + requestType);
        }
    }
}
