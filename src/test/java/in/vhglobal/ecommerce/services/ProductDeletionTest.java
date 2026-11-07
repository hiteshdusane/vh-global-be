package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.entities.products.Product;
import in.vhglobal.ecommerce.entities.products.ProductAdditionalInfo;
import in.vhglobal.ecommerce.entities.products.ProductStatus;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductAdditionalInfoRepository;
import in.vhglobal.ecommerce.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductDeletionTest {

    private ProductRepository repository;
    private ProductAdditionalInfoRepository additionalInfoRepository;
    private ImageStorageService imageStorage;
    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        additionalInfoRepository = mock(ProductAdditionalInfoRepository.class);
        imageStorage = mock(ImageStorageService.class);
        service = new ProductServiceImpl(repository, null, null, imageStorage, additionalInfoRepository);
    }

    @ParameterizedTest
    @EnumSource(ProductStatus.class)
    @NullSource
    void softDeletePreservesRowsAndImagesIncludingLegacyAndAlreadyDeleted(ProductStatus previousStatus) {
        Product product = Product.builder().productId("product").status(previousStatus)
                .imagePath("primary.jpg").build();
        when(repository.findById("product")).thenReturn(Optional.of(product));

        service.deleteProduct("product", false);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
        assertThat(product.getImagePath()).isEqualTo("primary.jpg");
        verify(repository).save(product);
        verify(repository, never()).delete(any(Product.class));
        verifyNoInteractions(imageStorage, additionalInfoRepository);
    }

    @Test
    void hardDeleteRemovesAlreadySoftDeletedProductAndItsImagesAndAdditionalInfo() {
        ProductAdditionalInfo info = new ProductAdditionalInfo();
        info.setImagePath("additional.jpg");
        List<ProductAdditionalInfo> infos = List.of(info);
        Product product = Product.builder().productId("product").status(ProductStatus.DELETED)
                .imagePath("primary.jpg").productAdditionalInfos(infos).build();
        when(repository.findById("product")).thenReturn(Optional.of(product));

        service.deleteProduct("product", true);

        verify(imageStorage).delete("primary.jpg");
        verify(imageStorage).delete("additional.jpg");
        verify(additionalInfoRepository).deleteAll(infos);
        verify(repository).delete(product);
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void hardDeleteHandlesMissingImagesAndNullAdditionalInfo() {
        Product product = Product.builder().productId("product").status(null).build();
        when(repository.findById("product")).thenReturn(Optional.of(product));

        service.deleteProduct("product", true);

        verify(repository).delete(product);
        verifyNoInteractions(imageStorage, additionalInfoRepository);
    }

    @Test
    void originalServiceOverloadDefaultsToSoftDelete() {
        Product product = Product.builder().productId("product").build();
        when(repository.findById("product")).thenReturn(Optional.of(product));

        service.deleteProduct("product");

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
        verify(repository).save(product);
        verify(repository, never()).delete(any(Product.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void missingProductDoesNotChangeAnything(boolean hardDelete) {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteProduct("missing", hardDelete))
                .isInstanceOf(NotFoundApiException.class);

        verify(repository, never()).save(any(Product.class));
        verify(repository, never()).delete(any(Product.class));
        verifyNoInteractions(imageStorage, additionalInfoRepository);
    }
}
