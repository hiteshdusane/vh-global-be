package in.vhglobal.ecommerce.dtos.mappers;

import in.vhglobal.ecommerce.dtos.reponse.ProductMainCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;
import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMainCategoryMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    ProductMainCategory toEntity(ProductMainCategoryRequest dto);

    ProductMainCategoryResponse toResponse(ProductMainCategory entity);
}
