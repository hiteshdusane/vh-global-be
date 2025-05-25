package in.vhglobal.ecommerce.dtos.request;

import lombok.Data;

@Data
public class PaginatedDataRequest {

    private Boolean hydrateMainCategory = false;

    private Integer page = 0;

    private Integer pageSize = 50;

    private String sortBy = "name";

    private String sortDi = "asc";
}
