package in.vhglobal.ecommerce.dtos.reponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductMainCategoryResponse {
    private String productMainCategoryId;
    private String name;
    private String shortDescription;
    private String longDescription;
}
