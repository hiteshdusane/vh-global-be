package in.vhglobal.ecommerce.dtos.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String productId;
    private String name;
    private String shortDescription;
    private String longDescription;
    private Double price;
    private Integer minimumQuantity;
    private ProductSubCategoryResponse productSubCategory;
    private String primaryImageUrl;
    private Set<String> additionalImageUrls;
}
