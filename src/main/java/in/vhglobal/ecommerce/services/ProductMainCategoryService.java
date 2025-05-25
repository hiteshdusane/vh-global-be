package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductMainCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;

import java.util.List;

public interface ProductMainCategoryService {

    ProductMainCategoryResponse create(ProductMainCategoryRequest request);

    ProductMainCategoryResponse getById(String id);

    List<ProductMainCategoryResponse> getAll();

    ProductMainCategoryResponse update(String id, ProductMainCategoryRequest request);

    void delete(String id);
}
