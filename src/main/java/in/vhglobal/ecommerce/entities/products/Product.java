package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private String productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "long_description", length = 1000)
    private String longDescription;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sub_category_id", nullable = false)
    private ProductSubCategory productSubCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductAdditionalInfo> productAdditionalInfos;
}
