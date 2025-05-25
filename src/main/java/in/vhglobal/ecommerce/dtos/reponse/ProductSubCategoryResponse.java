package in.vhglobal.ecommerce.dtos.reponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubCategoryResponse {
    private String productSubCategoryId;
    private String name;
    private String shortDescription;
    private String longDescription;
    private ProductMainCategoryResponse productMainCategory;
}