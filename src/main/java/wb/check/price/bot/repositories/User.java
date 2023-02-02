package wb.check.price.bot.repositories;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private long id;
    private String userName;
    private String firstName;
    private String lastName;
    private LocalDateTime date;

    @OneToMany(mappedBy = "userId")
    private List<Product> products = new ArrayList<>();
    private int discount;

    public User() {
        this.discount = 0;
    }
}
