package in.vhglobal.ecommerce.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMainCategoryRequest {

    @NotNull(message = "Invalid Main Category name")
    @NotBlank(message = "Main Category name is blank")
    private String name;
    private String shortDescription;
    private String longDescription;
}
