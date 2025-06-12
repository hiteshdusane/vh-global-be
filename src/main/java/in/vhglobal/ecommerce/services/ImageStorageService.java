package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.config.StorageProperties;
import in.vhglobal.ecommerce.exceptions.InternalServerErrorApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final StorageProperties storageProperties;

    public String store(MultipartFile file, String fileName) throws IOException {
        String folder = storageProperties.getImagePath();
        Files.createDirectories(Paths.get(folder));
        String imagePath = new File(storageProperties.getImagePath()).getAbsolutePath();
        log.info("Image will be stored to path: {}", imagePath);
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".")))
                .orElse(".png");
        if (Objects.isNull(fileName) || file.isEmpty() || fileName.isBlank()) {
            fileName = UUID.randomUUID().toString();
        }
        fileName = fileName + ext;
        Path path = Paths.get(folder, fileName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public Resource load(String fileName) throws IOException {
        Path path = Paths.get(storageProperties.getImagePath(), fileName);
        if (!Files.exists(path)) throw new FileNotFoundException("File not found");

        return new UrlResource(path.toUri());
    }

    public void delete(String fileName) {
        String folder = storageProperties.getImagePath();
        String imagePath = Paths.get(folder, fileName).toString();
        log.warn("Deleting file: {} from folder: {}", fileName, folder);
        try {
            Files.deleteIfExists(Path.of(imagePath));
        } catch (IOException e) {
            log.warn("", e);
            throw new InternalServerErrorApiException("Failed to delete image file: " + fileName);
        }
    }
}
