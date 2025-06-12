package in.vhglobal.ecommerce.dtos.mappers;

import in.vhglobal.ecommerce.dtos.reponse.ProductResponse;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import in.vhglobal.ecommerce.entities.products.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Value("${image.base-url}")
    String baseUrl;

    public abstract Product toEntity(ProductRequest request);

    @Mapping(target = "additionalImageUrls", expression = "java(additionalImageUrls(product))")
    @Mapping(target = "primaryImageUrl", expression = "java(primaryImageUrl(product))")
    public abstract ProductResponse toResponse(Product product);

    Set<String> additionalImageUrls(Product product) {
        if (product.getProductAdditionalInfos() == null) return Collections.emptySet();
        return product.getProductAdditionalInfos().stream()
                .map(info -> baseUrl + info.getImagePath())  // Assumes image filename is stored
                .collect(Collectors.toSet());
    }

    String primaryImageUrl(Product product) {
        if (product.getImagePath() == null) return null;
        return baseUrl + product.getImagePath();
    }
}
