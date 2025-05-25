package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "product_sub_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_sub_category_id")
    private String productSubCategoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_main_category_id", nullable = false)
    private ProductMainCategory productMainCategory;

    @ToString.Exclude
    @OneToMany(mappedBy = "productSubCategory", cascade = CascadeType.ALL)
    private List<Product> products;
}
