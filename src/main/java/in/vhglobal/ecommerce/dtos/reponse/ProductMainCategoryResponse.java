package in.vhglobal.ecommerce.dtos.reponse;

import lombok.*;
import in.vhglobal.ecommerce.entities.products.ProductStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductMainCategoryResponse {
    private String productMainCategoryId;
    private String name;
    private ProductStatus status;
    private String shortDescription;
    private String longDescription;
}
