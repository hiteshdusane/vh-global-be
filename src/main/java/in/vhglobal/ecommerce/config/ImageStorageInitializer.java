package in.vhglobal.ecommerce.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageStorageInitializer {

    private final StorageProperties storageProperties;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(storageProperties.getImagePath()));
        log.info("Image storage directory ready: {} ", Paths.get(storageProperties.getImagePath()).toAbsolutePath());
    }
}
