package in.vhglobal.ecommerce.controllers;

import in.vhglobal.ecommerce.dtos.reponse.*;
import in.vhglobal.ecommerce.dtos.request.PaginatedDataRequest;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import in.vhglobal.ecommerce.exceptions.InternalServerErrorApiException;
import in.vhglobal.ecommerce.services.ProductAdditionalInfoServiceImpl;
import in.vhglobal.ecommerce.services.ProductMainCategoryService;
import in.vhglobal.ecommerce.services.ProductService;
import in.vhglobal.ecommerce.services.ProductSubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsController {

    private final ProductMainCategoryService productMainCategoryService;
    private final ProductSubCategoryService productSubCategoryService;
    private final ProductService productService;
    private final ProductAdditionalInfoServiceImpl productAdditionalInfoService;

    @Operation(summary = "Create a new product", description = "Adds a new product to the catalog", tags = "Product")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(request), HttpStatus.CREATED.value(), "product.created"));
    }

    @Operation(summary = "Upload image for product", tags = "Product")
    @PostMapping("/upload-image/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadImageForProduct(@PathVariable("productId") String productId, @RequestParam("file") MultipartFile file) {
        try {
            ProductResponse imageStoredProductResponse = productService.uploadImageForProduct(productId, file);
            return ResponseEntity.ok(ApiResponse.success(String.format("Image uploaded for product id: %s", productId), imageStoredProductResponse, 200));
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            throw new InternalServerErrorApiException(String.format("Failed to upload image for product id: %s", productId));
        }
    }

    @Operation(summary = "Get product by ID", tags = "Product")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched", productService.getProductById(productId), HttpStatus.OK.value()));
    }

    @Operation(summary = "Delete product by ID", tags = "Product")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProductById(@PathVariable String productId) {
        log.info("Deleting product with id: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Delete Request Accepted", "Request Accepted", HttpStatus.ACCEPTED.value()));
    }

    @Operation(summary = "Get all products paginated", tags = "Product")
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProducts(@RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductResponse> result = productService.getAllProducts(pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success("Fetched all products", paginatedResponse, HttpStatus.OK.value()));
    }

    @Operation(summary = "Get all products by sub-category", tags = "Product")
    @PostMapping("/by-sub-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProductsBySubCategory(@RequestParam("subProductCategoryId") String subProductCategoryId, @RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductResponse> result = productService.getBySubCategoryId(subProductCategoryId, pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success(String.format("Fetched all products by sub category: %s", subProductCategoryId), paginatedResponse, HttpStatus.OK.value()));
    }

    @Operation(summary = "Create product main category", tags = "Product Main Category")
    @PostMapping("/main-category")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> createProductMainCategory(@RequestBody @Valid ProductMainCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category created", productMainCategoryService.create(request), HttpStatus.CREATED.value()));
    }

    @Operation(summary = "Get main category by ID", tags = "Product Main Category")
    @GetMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> getProductMainCategoryById(@PathVariable String id) {
        ProductMainCategoryResponse productMainCategoryResponse = productMainCategoryService.getById(id);
        log.info("productMainCategoryResponse received: {}", productMainCategoryResponse);
        return ResponseEntity.ok(ApiResponse.success("Category fetched", productMainCategoryResponse, HttpStatus.OK.value()));
    }

    @Operation(summary = "Get all main categories", tags = "Product Main Category")
    @GetMapping("/main-category")
    public ResponseEntity<ApiResponse<List<ProductMainCategoryResponse>>> getAllProductMainCategories() {
        return ResponseEntity.ok(ApiResponse.success("All categories fetched", productMainCategoryService.getAll(), HttpStatus.OK.value()));
    }

    @Operation(summary = "Update product main category", tags = "Product Main Category")
    @PutMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<ProductMainCategoryResponse>> updateProductMainCategory(@PathVariable String id, @RequestBody @Valid ProductMainCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", productMainCategoryService.update(id, request), HttpStatus.ACCEPTED.value()));
    }

    @Operation(summary = "Delete product main category", tags = "Product Main Category")
    @DeleteMapping("/main-category/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProductMainCategory(@PathVariable String id) {
        productMainCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", id, HttpStatus.OK.value()));
    }


    @Operation(summary = "Create sub-category", tags = "Product Sub Category")
    @PostMapping("/sub-category")
    public ResponseEntity<ApiResponse<ProductSubCategoryResponse>> createProductSubCategory(@RequestBody @Valid ProductSubCategoryRequest request) {
        ProductSubCategoryResponse response = productSubCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sub-category created successfully", response, HttpStatus.CREATED.value()));
    }

    @Operation(summary = "Get all sub-categories", tags = "Product Sub Category")
    @PostMapping("/sub-category-all")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductSubCategoryResponse>>> getAllProductSubCategories(@RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductSubCategoryResponse> result = productSubCategoryService.getAll(pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductSubCategoryResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success("Fetched all sub-categories", paginatedResponse, HttpStatus.OK.value()));
    }

    @Operation(summary = "Get sub-category by ID", tags = "Product Sub Category")
    @GetMapping("/sub-category/{id}")
    public ResponseEntity<ApiResponse<ProductSubCategoryResponse>> getProductSubCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.success("Sub-category fetched", productSubCategoryService.getById(id), HttpStatus.OK.value()));
    }

    @Operation(summary = "Get all sub-categories by main category", tags = "Product Sub Category")
    @PostMapping("/sub-category-by-main-category")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductSubCategoryResponse>>> getAllProductSubCategoriesByMainCategory(@RequestParam("mainProductCategoryId") String mainProductCategoryId, @RequestBody @Valid PaginatedDataRequest paginatedDataRequest) {

        Sort sort = paginatedDataRequest.getSortDi().equalsIgnoreCase("desc") ? Sort.by(paginatedDataRequest.getSortBy()).descending() : Sort.by(paginatedDataRequest.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(paginatedDataRequest.getPage(), paginatedDataRequest.getPageSize(), sort);
        Page<ProductSubCategoryResponse> result = productSubCategoryService.getByMainCategoryId(mainProductCategoryId, pageable, paginatedDataRequest.getHydrateMainCategory());

        PaginatedResponse<ProductSubCategoryResponse> paginatedResponse = PaginatedResponse.fromPage(result);
        return ResponseEntity.ok(
                ApiResponse.success(String.format("Fetched all sub-categories by main category: %s", mainProductCategoryId), paginatedResponse, HttpStatus.OK.value()));
    }

    @Operation(summary = "Upload image to additional info")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImageInAdditionalInfo(@RequestParam("productId") String productId, @RequestParam("file") MultipartFile file) {
        try {
            String imageName = productAdditionalInfoService.uploadImage(productId, file);
            return ResponseEntity.ok(ApiResponse.success("Image uploaded", imageName, 200));
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            throw new InternalServerErrorApiException(String.format("Failed to upload image for product id: %s", productId));
        }
    }

    @Operation(summary = "Upload multiple images to additional info", tags = "Product")
    @PostMapping("/upload-multiple")
    public ResponseEntity<ApiResponse<String>> uploadMultipleImagesInAdditionalInfo(@RequestParam("productId") String productId, @RequestParam("file") List<MultipartFile> files) {
        productAdditionalInfoService.uploadImage(productId, files);
        return ResponseEntity.ok(ApiResponse.success("Request Accepted", "Request Queued", HttpStatus.ACCEPTED.value()));
    }

    @Operation(summary = "Serve product image")
    @GetMapping("/get-image/{imageName}")
    public ResponseEntity<Resource> download(@PathVariable String imageName) throws IOException {
        Resource file = productAdditionalInfoService.getImage(imageName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }

    @Operation(summary = "Delete additional product info image", tags = "Product Additional Info")
    @DeleteMapping("/product-additional-info/{productAdditionalInfoId}")
    public ResponseEntity<ApiResponse<String>> deleteProductAdditionalInfoId(@PathVariable String productAdditionalInfoId) {
        productAdditionalInfoService.deleteProductAdditionalInfoById(productAdditionalInfoId);
        return ResponseEntity.ok(ApiResponse.success("Product Additional Info deleted successfully", productAdditionalInfoId, HttpStatus.OK.value()));
    }

}
