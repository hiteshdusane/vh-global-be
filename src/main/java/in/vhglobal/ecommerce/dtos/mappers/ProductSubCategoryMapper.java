package in.vhglobal.ecommerce.dtos.mappers;

import in.vhglobal.ecommerce.dtos.reponse.ProductSubCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductSubCategoryMapper {

    ProductSubCategory toEntity(ProductSubCategoryRequest request);

    ProductSubCategoryResponse toResponse(ProductSubCategory entity);
}
