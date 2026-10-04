package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_sub_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"productMainCategory", "products"})
public class ProductSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_sub_category_id")
    private String productSubCategoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "long_description", length = 1000)
    private String longDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_main_category_id", nullable = false)
    private ProductMainCategory productMainCategory;

    @OneToMany(mappedBy = "productSubCategory", cascade = CascadeType.ALL)
    private List<Product> products;
}
