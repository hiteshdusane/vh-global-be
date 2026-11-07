package in.vhglobal.ecommerce.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPaginationRequest extends PaginatedDataRequest {

    private Boolean includeDeleted = false;
}
