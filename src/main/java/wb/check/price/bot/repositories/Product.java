package wb.check.price.bot.repositories;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private UUID id;
    private long wbId;
    private long userId;
    private String name;
    private int price;
    
    public Product() {
        this.id = UUID.randomUUID();
    }
}
