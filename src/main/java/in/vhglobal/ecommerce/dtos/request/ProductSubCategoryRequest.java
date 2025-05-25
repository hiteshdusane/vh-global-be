package in.vhglobal.ecommerce.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubCategoryRequest {

    @NotNull(message = "Invalid Sub Category name")
    @NotBlank(message = "Sub Category name is blank")
    private String name;

    private String shortDescription;

    private String longDescription;

    @NotNull(message = "Invalid main category for sub category")
    @NotBlank(message = "Main category for sub category is blank")
    private String productMainCategoryId;
}