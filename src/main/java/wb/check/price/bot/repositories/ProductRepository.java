package wb.check.price.bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByUserId(long userId);
}
