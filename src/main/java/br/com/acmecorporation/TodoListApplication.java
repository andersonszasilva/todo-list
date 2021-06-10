package br.com.acmecorporation;

import br.com.acmecorporation.user.domain.Authority;
import br.com.acmecorporation.user.domain.User;
import br.com.acmecorporation.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class TodoListApplication {

	private UserRepository userRepository;

	@Autowired
	public TodoListApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(TodoListApplication.class, args);
	}

	@PostConstruct
	public void init(){

		Authority authorityUser = new Authority("USER");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		User user1 = new User("wile.coyote@acmecorporation.com", passwordEncoder.encode("123456"), "Wile E. Coyote");
		user1.setAuthorities(Arrays.asList(authorityUser));
		User user2 = new User("road.runner@acmecorporation.com", passwordEncoder.encode("123456"), "Road Runner");
		user2.setAuthorities(Arrays.asList(authorityUser));
		User superUser = new User("superuser@acmecorporation.com", passwordEncoder.encode("123456"), "Super User");
		superUser.setAuthorities(Arrays.asList(new Authority("SUPER_USER")));

		userRepository.saveAll(Arrays.asList(user1, user2, superUser));
	}
}
