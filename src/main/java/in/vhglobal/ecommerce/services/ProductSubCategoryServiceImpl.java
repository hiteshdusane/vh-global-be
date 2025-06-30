package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.mappers.ProductSubCategoryMapper;
import in.vhglobal.ecommerce.dtos.reponse.ProductSubCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import in.vhglobal.ecommerce.exceptions.BadRequestApiException;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductMainCategoryRepository;
import in.vhglobal.ecommerce.repositories.ProductRepository;
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
    private final ProductRepository productRepository;

    @Override
    public ProductSubCategoryResponse create(ProductSubCategoryRequest request) {
        ProductMainCategory mainCategory = mainCategoryRepository.findById(request.getProductMainCategoryId())
                .orElseThrow(() -> new BadRequestApiException("Main category not found"));

        ProductSubCategory entity = mapper.toEntity(request);
        entity.setProductMainCategory(mainCategory);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Page<ProductSubCategoryResponse> getAll(Pageable pageable, boolean hydrateMainCategory) {
        Page<ProductSubCategory> page = repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public ProductSubCategoryResponse getById(String productSubCategoryId) {
        return repository.findByIdWithMainCategory(productSubCategoryId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundApiException("Sub category not found"));
    }

    @Override
    public Page<ProductSubCategoryResponse> getByMainCategoryId(String mainCategoryId, Pageable pageable, boolean hydrateMainCategory) {
        log.info("Finding sub categories by main category id: {}", mainCategoryId);
        ProductMainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new BadRequestApiException("Main category not found"));
        log.info("Main category found: {}", mainCategory);
        Page<ProductSubCategory> page = repository
                .findByProductMainCategory_ProductMainCategoryId(mainCategoryId, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public void deleteProductSubCategory(String subProductCategoryId) {
        log.info("Deleting sub product with id: {}", subProductCategoryId);
        ProductSubCategory existing = repository.getReferenceById(subProductCategoryId);
        if (Objects.nonNull(existing.getProducts()) && !existing.getProducts().isEmpty()) {
            log.info("Deleting products: {}", existing.getProducts().size());
            productRepository.deleteAll(existing.getProducts());
        }
        repository.delete(existing);
    }

}
