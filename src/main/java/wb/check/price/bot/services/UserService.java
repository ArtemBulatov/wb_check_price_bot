package wb.check.price.bot.services;

import wb.check.price.bot.repositories.User;
import wb.check.price.bot.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class UserService {
    private final UserRepository usersRepository;

    public UserService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User save(User user) {
        user.setDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        return usersRepository.save(user);
    }

    public User get(long id) {
        return usersRepository.findById(id).orElse(new User());
    }
    public List<User> getAll() {
        return usersRepository.findAll();
    }
}
