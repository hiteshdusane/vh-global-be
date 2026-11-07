package in.vhglobal.ecommerce.dtos.reponse;

import lombok.*;
import in.vhglobal.ecommerce.entities.products.ProductStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubCategoryResponse {
    private String productSubCategoryId;
    private String name;
    private ProductStatus status;
    private String shortDescription;
    private String longDescription;
    private ProductMainCategoryResponse productMainCategory;
}
