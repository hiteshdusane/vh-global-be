package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.mappers.ProductMapper;
import in.vhglobal.ecommerce.dtos.reponse.ProductResponse;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import in.vhglobal.ecommerce.entities.products.Product;
import in.vhglobal.ecommerce.entities.products.ProductAdditionalInfo;
import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import in.vhglobal.ecommerce.exceptions.BadRequestApiException;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductAdditionalInfoRepository;
import in.vhglobal.ecommerce.repositories.ProductRepository;
import in.vhglobal.ecommerce.repositories.ProductSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSubCategoryRepository productSubCategoryRepository;
    private final ImageStorageService imageStorageService;
    private final ProductAdditionalInfoRepository productAdditionalInfoRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        ProductSubCategory productSubCategory = productSubCategoryRepository.findById(request.getProductSubCategoryId())
                .orElseThrow(() -> new BadRequestApiException("Sub category not found"));
        Product product = productMapper.toEntity(request);
        product.setProductSubCategory(productSubCategory);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse uploadImageForProduct(String productId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundApiException(String.format("Product not found with id: %s", productId)));
        if (Objects.nonNull(product.getImagePath())) {
            log.info("Product already has image uploaded for it, replacing it with the new one.");
        }
        String imagePath = imageStorageService.store(file, product.getProductId());
        product.setImagePath(imagePath);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundApiException(String.format("Product not found with id: %s", id)));
        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, boolean hydrateSubCategory) {
        Page<Product> page = productRepository.findAll(pageable);
        return page.map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> getBySubCategoryId(String subCategoryId, Pageable pageable, boolean hydrateSubCategory) {
        log.info("Finding products by sub category id: {}", subCategoryId);
        ProductSubCategory subCategory = productSubCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new BadRequestApiException("Main category not found"));
        log.info("Sub category found: {}", subCategory);
        Page<Product> page = productRepository
                .findByProductSubCategory_ProductSubCategoryId(subCategoryId, pageable);
        return page.map(productMapper::toResponse);
    }

    @Override
    @Async
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundApiException("Product not found with ID: " + productId));
        log.info("Deleting product: {}, and its additional info", product.getName());
        // Delete images from a file system
        if (Objects.nonNull(product.getImagePath()) && !product.getImagePath().isBlank()) {
            imageStorageService.delete(product.getImagePath());
        }
        List<ProductAdditionalInfo> additionalInfos = product.getProductAdditionalInfos();
        if (Objects.nonNull(additionalInfos) && !additionalInfos.isEmpty()) {
            log.info("Deleting additional {} infos of product: {}", additionalInfos.size(), product.getName());
            additionalInfos.forEach(productAdditionalInfo -> {
                imageStorageService.delete(productAdditionalInfo.getImagePath());
            });
        }
        log.info("Deleted images from storage");
        productAdditionalInfoRepository.deleteAll(additionalInfos);
        log.info("Deleted additional info");
        productRepository.delete(product);
        log.info("Deleted product");
    }
}
