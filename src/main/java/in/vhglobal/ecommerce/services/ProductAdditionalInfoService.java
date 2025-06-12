package in.vhglobal.ecommerce.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductAdditionalInfoService {

    String uploadImage(String productId, MultipartFile file) throws IOException;

    Resource getImage(String imageName) throws IOException;

    void uploadImage(String productId, List<MultipartFile> files);

    void deleteProductAdditionalInfoById(String productAdditionalInfoId);
}
