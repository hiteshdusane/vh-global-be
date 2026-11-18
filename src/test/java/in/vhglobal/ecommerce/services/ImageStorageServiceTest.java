package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.config.StorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ImageStorageServiceTest {
    @TempDir Path directory;

    @Test
    void closesInputAfterSuccessfulCopy() throws IOException {
        byte[] content = {1, 2, 3};
        var input = spy(new ByteArrayInputStream(content));
        var file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getInputStream()).thenReturn(input);

        assertThat(service().store(file, "product")).isEqualTo("product.png");
        assertThat(Files.readAllBytes(directory.resolve("product.png"))).containsExactly(content);
        verify(input).close();
    }

    @Test
    void closesInputWhenDestinationCannotBeWritten() throws IOException {
        Files.createDirectory(directory.resolve("product.png"));
        Files.writeString(directory.resolve("product.png/child"), "prevent directory replacement");
        var input = spy(new ByteArrayInputStream(new byte[]{1}));
        var file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getInputStream()).thenReturn(input);

        assertThatThrownBy(() -> service().store(file, "product")).isInstanceOf(IOException.class);
        verify(input).close();
    }

    private ImageStorageService service() {
        var properties = new StorageProperties();
        properties.setImagePath(directory.toString());
        return new ImageStorageService(properties);
    }
}
