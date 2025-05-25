package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.mappers.ProductMainCategoryMapper;
import in.vhglobal.ecommerce.dtos.reponse.ProductMainCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;
import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductMainCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductMainCategoryServiceImpl implements ProductMainCategoryService {

    private final ProductMainCategoryRepository repository;
    private final ProductMainCategoryMapper productMainCategoryMapper;

    @Override
    public ProductMainCategoryResponse create(ProductMainCategoryRequest request) {
        ProductMainCategory productMainCategory = productMainCategoryMapper.toEntity(request);
        log.info("Saving productMainCategory: {}", productMainCategory);
        ProductMainCategory productMainCategorySaved = repository.save(productMainCategory);
        log.info("Saved productMainCategory: {}", productMainCategorySaved);
        return productMainCategoryMapper.toResponse(productMainCategorySaved);
    }

    @Override
    public ProductMainCategoryResponse getById(String id) {
        try {
            ProductMainCategory productMainCategory = repository.getReferenceById(id);
            return productMainCategoryMapper.toResponse(productMainCategory);
        } catch (EntityNotFoundException e) {
            throw new NotFoundApiException("Product Main Category Not Found for id: " + id);
        }
    }

    @Override
    public List<ProductMainCategoryResponse> getAll() {
        return repository.findAll().stream().map(productMainCategoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductMainCategoryResponse update(String id, ProductMainCategoryRequest request) {
        ProductMainCategory existing = repository.getReferenceById(id);
        existing.setName(request.getName());
        existing.setShortDescription(request.getShortDescription());
        existing.setLongDescription(request.getLongDescription());
        ProductMainCategory updated = repository.save(existing);
        log.info("Updated ProductMainCategory: {}", updated);
        return productMainCategoryMapper.toResponse(updated);
    }

    @Override
    public void delete(String id) {
        log.info("Deleting main product with id: {}", id);
        ProductMainCategory existing = repository.getReferenceById(id);
        repository.delete(existing);
    }
}
