package wb.check.price.bot.services;

import wb.check.price.bot.repositories.Product;
import wb.check.price.bot.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    public void deleteAllByUserId(long userId) {
        productRepository.findAllByUserId(userId).forEach(productRepository::delete);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
