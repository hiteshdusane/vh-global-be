package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.mappers.ProductSubCategoryMapper;
import in.vhglobal.ecommerce.dtos.reponse.ProductSubCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import in.vhglobal.ecommerce.exceptions.BadRequestApiException;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductMainCategoryRepository;
import in.vhglobal.ecommerce.entities.products.ProductStatus;
import org.springframework.transaction.annotation.Transactional;
import in.vhglobal.ecommerce.repositories.ProductSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSubCategoryServiceImpl implements ProductSubCategoryService {

    private final ProductSubCategoryRepository repository;
    private final ProductMainCategoryRepository mainCategoryRepository;
    private final ProductSubCategoryMapper mapper;
    private final ProductService productService;

    @Override
    public ProductSubCategoryResponse create(ProductSubCategoryRequest request) {
        ProductMainCategory mainCategory = mainCategoryRepository.findById(request.getProductMainCategoryId())
                .orElseThrow(() -> new BadRequestApiException("Main category not found"));

        ProductSubCategory entity = mapper.toEntity(request);
        entity.setProductMainCategory(mainCategory);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Page<ProductSubCategoryResponse> getAll(Pageable pageable, boolean hydrateMainCategory, boolean includeDeleted) {
        Page<ProductSubCategory> page = repository.findAllVisible(includeDeleted, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public ProductSubCategoryResponse getById(String productSubCategoryId, boolean includeDeleted) {
        return repository.findVisibleById(productSubCategoryId, includeDeleted)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundApiException("Sub category not found"));
    }

    @Override
    public Page<ProductSubCategoryResponse> getByMainCategoryId(String mainCategoryId, Pageable pageable, boolean hydrateMainCategory, boolean includeDeleted) {
        log.info("Finding sub categories by main category id: {}", mainCategoryId);
        ProductMainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new BadRequestApiException("Main category not found"));
        log.info("Main category found with id: {}", mainCategory.getProductMainCategoryId());
        Page<ProductSubCategory> page = repository
                .findVisibleByMainCategoryId(mainCategoryId, includeDeleted, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteProductSubCategory(String subProductCategoryId, boolean hardDelete) {
        log.info("Deleting sub product with id: {}", subProductCategoryId);
        ProductSubCategory existing = repository.findById(subProductCategoryId)
                .orElseThrow(() -> new NotFoundApiException("Sub category not found"));
        if (Objects.nonNull(existing.getProducts()) && !existing.getProducts().isEmpty()) {
            log.info("Deleting products: {}", existing.getProducts().size());
            existing.getProducts().forEach(product ->
                    productService.deleteProductSynchronously(product.getProductId(), hardDelete));
        }
        if (hardDelete) {
            repository.delete(existing);
        } else {
            existing.setStatus(ProductStatus.DELETED);
            repository.save(existing);
        }
    }

}
