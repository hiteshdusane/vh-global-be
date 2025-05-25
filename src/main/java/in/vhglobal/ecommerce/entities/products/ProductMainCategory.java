package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "product_main_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_main_category_id")
    private String productMainCategoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @ToString.Exclude
    @OneToMany(mappedBy = "productMainCategory", cascade = CascadeType.ALL)
    private List<ProductSubCategory> productSubCategories;
}
