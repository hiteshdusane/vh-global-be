package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.entities.products.Product;
import in.vhglobal.ecommerce.entities.products.ProductAdditionalInfo;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.ProductAdditionalInfoRepository;
import in.vhglobal.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdditionalInfoServiceImpl implements ProductAdditionalInfoService {

    private final ProductRepository productRepository;
    private final ProductAdditionalInfoRepository infoRepository;
    private final ImageStorageService imageStorageService;

    @Override
    public String uploadImage(String productId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundApiException(String.format("Product with id: %s not found", productId)));

        String storedFileName = imageStorageService.store(file, "");

        ProductAdditionalInfo info = ProductAdditionalInfo.builder()
                .imagePath(storedFileName)
                .product(product)
                .build();
        infoRepository.save(info);
        return storedFileName;
    }

    @Override
    public Resource getImage(String imageName) throws IOException {
        return imageStorageService.load(imageName);
    }

    @Override
    @Async
    public void uploadImage(String productId, List<MultipartFile> files) {
        AtomicInteger failedUploads = new AtomicInteger(0);
        List<ProductAdditionalInfo> productAdditionalInfos = new ArrayList<>();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundApiException(String.format("Product with id: %s not found", productId)));
            log.info("Uploading {} image/s for product: {}", files.size(), product.getName());
            files.forEach(file -> {
                try {
                    String storedFileName = imageStorageService.store(file, "");
                    log.info("Stored image with name: {}", storedFileName);
                    productAdditionalInfos.add(ProductAdditionalInfo.builder()
                            .imagePath(storedFileName)
                            .product(product)
                            .build());
                } catch (IOException e) {
                    log.warn("Error occurred while uploading file: {}", file.getName());
                    failedUploads.getAndIncrement();
                }
            });
        }
        infoRepository.saveAll(productAdditionalInfos);
        log.info("Failed uploads: {}", failedUploads.get());
    }

    @Override
    public void deleteProductAdditionalInfoById(String productAdditionalInfoId) {
        ProductAdditionalInfo info = infoRepository.findById(productAdditionalInfoId)
                .orElseThrow(() -> new NotFoundApiException("Product Additional info not found with id: " + productAdditionalInfoId));
        imageStorageService.delete(info.getImagePath());
        infoRepository.delete(info);
    }

}
