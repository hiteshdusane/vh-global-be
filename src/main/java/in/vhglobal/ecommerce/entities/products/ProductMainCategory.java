package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_main_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "productSubCategories")
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

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @OneToMany(mappedBy = "productMainCategory", cascade = CascadeType.ALL)
    private List<ProductSubCategory> productSubCategories;
}
