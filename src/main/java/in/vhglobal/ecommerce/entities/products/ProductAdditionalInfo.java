package in.vhglobal.ecommerce.entities.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_additional_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAdditionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productAdditionalInfoId;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
