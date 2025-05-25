package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductSubCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSubCategoryService {

    ProductSubCategoryResponse create(ProductSubCategoryRequest request);

    Page<ProductSubCategoryResponse> getAll(Pageable pageable, boolean hydrateMainCategory);

    ProductSubCategoryResponse getById(String productSubCategoryId);

    Page<ProductSubCategoryResponse> getByMainCategoryId(String mainCategoryId, Pageable pageable, boolean hydrateMainCategory);
}
