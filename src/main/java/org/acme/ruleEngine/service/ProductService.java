package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.dao.ProductDao;
import org.acme.models.Product;
import org.acme.records.ServiceResponse;

@ApplicationScoped
public class ProductService {
    private final ProductDao productDao;

    @Inject
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public ServiceResponse createProduct(Product product) {
        boolean isExists = productDao.isProductExist(product.accountId(), product.name());
        if (!isExists) {
            int productId = productDao.createProduct(product);
            return (productId > 0) ? new ServiceResponse(Response.Status.CREATED.getStatusCode(), true, "Product created successfully") : new ServiceResponse(Response.Status.EXPECTATION_FAILED.getStatusCode(), false, "cannot create product");

        } else
            return new ServiceResponse(Response.Status.CONFLICT.getStatusCode(), false, "product already exists with that name");

    }
}
