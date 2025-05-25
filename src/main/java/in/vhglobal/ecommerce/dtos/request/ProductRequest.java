package in.vhglobal.ecommerce.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotNull(message = "Invalid Product name")
    @NotBlank(message = "Product name is blank")
    private String name;

    private String shortDescription;

    private String longDescription;

    @NotNull(message = "Invalid product price")
    @Min(value = 1, message = "Product price should be more than 0")
    private Double price;

    @NotNull(message = "Invalid product quantity")
    @Min(value = 1, message = "Product quantity should be more than 0")
    private Integer minimumQuantity;

    @NotNull(message = "Invalid sub category for product")
    @NotBlank(message = "Sub category for product is blank")
    private String productSubCategoryId;

}
