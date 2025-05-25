package in.vhglobal.ecommerce.controllers;

import in.vhglobal.ecommerce.dtos.reponse.*;
import in.vhglobal.ecommerce.dtos.request.PaginatedDataRequest;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import in.vhglobal.ecommerce.services.ProductMainCategoryService;
import in.vhglobal.ecommerce.services.ProductService;
import in.vhglobal.ecommerce.services.ProductSubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsController {

    private final ProductMainCategoryService productMainCategoryService;
    private final ProductSubCategoryService productSubCategoryService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(request), HttpStatus.CREATED.value(), "product.created"));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getProductById(productId), HttpStatus.OK.value()));
    }

    @PostMapping("/all")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProducts(@RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductResponse> result = productService.getAllProducts(pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success("Fetched all products", paginatedResponse, HttpStatus.OK.value()));
    }

    @PostMapping("/by-sub-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProductsBySubCategory(@RequestParam("subProductCategoryId") String subProductCategoryId, @RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductResponse> result = productService.getBySubCategoryId(subProductCategoryId, pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success(String.format("Fetched all products by sub category: %s", subProductCategoryId), paginatedResponse, HttpStatus.OK.value()));
    }

    @PostMapping("/main-category")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> createProductMainCategory(@RequestBody @Valid ProductMainCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category created", productMainCategoryService.create(request), HttpStatus.CREATED.value()));
    }

    @GetMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> getProductMainCategoryById(@PathVariable String id) {
        ProductMainCategoryResponse productMainCategoryResponse = productMainCategoryService.getById(id);
        log.info("productMainCategoryResponse received: {}", productMainCategoryResponse);
        return ResponseEntity.ok(ApiResponse.success("Category fetched", productMainCategoryResponse, HttpStatus.OK.value()));
    }

    @GetMapping("/main-category")
    public ResponseEntity<ApiResponse<List<ProductMainCategoryResponse>>> getAllProductMainCategories() {
        return ResponseEntity.ok(ApiResponse.success("All categories fetched", productMainCategoryService.getAll(), HttpStatus.OK.value()));
    }

    @PutMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> updateProductMainCategory(@PathVariable String id, @RequestBody @Valid ProductMainCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", productMainCategoryService.update(id, request), HttpStatus.ACCEPTED.value()));
    }

    @DeleteMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProductMainCategory(@PathVariable String id) {
        productMainCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", id, HttpStatus.OK.value()));
    }


    @PostMapping("/sub-category")
    public ResponseEntity<ApiResponse<ProductSubCategoryResponse>> createProductSubCategory(@RequestBody @Valid ProductSubCategoryRequest request) {
        ProductSubCategoryResponse response = productSubCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sub-category created successfully", response, HttpStatus.CREATED.value()));
    }

    @PostMapping("/sub-category-all")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductSubCategoryResponse>>> getAllProductSubCategories(@RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductSubCategoryResponse> result = productSubCategoryService.getAll(pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductSubCategoryResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success("Fetched all sub-categories", paginatedResponse, HttpStatus.OK.value()));
    }

    @GetMapping("/sub-category/{id}")
    public ResponseEntity<ApiResponse<ProductSubCategoryResponse>> getProductSubCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.success("Sub-category fetched", productSubCategoryService.getById(id), HttpStatus.OK.value()));
    }

    @PostMapping("/sub-category-by-main-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductSubCategoryResponse>>> getAllProductSubCategoriesByMainCategory(@RequestParam("mainProductCategoryId") String mainProductCategoryId, @RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductSubCategoryResponse> result = productSubCategoryService.getByMainCategoryId(mainProductCategoryId, pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductSubCategoryResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success(String.format("Fetched all sub-categories by main category: %s", mainProductCategoryId), paginatedResponse, HttpStatus.OK.value()));
    }
}
