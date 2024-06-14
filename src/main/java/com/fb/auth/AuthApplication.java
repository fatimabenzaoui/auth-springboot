package com.fb.auth;

import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	// retourne une expression lambda qui va s'exécuter au démarrage (@Bean)
	@Bean
	CommandLineRunner start(AuthorityRepository authorityRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			// crée les rôles
			Stream.of("ADMIN", "EDITOR", "CUSTOMER").forEach(r -> {
				Authority authority = new Authority();
				authority.setAuthorityLabel(r);
				// sauvegarde les rôles s'ils n'existent pas déjà
				Optional<Authority> existingAuthority = authorityRepository.findById(r);
				if (existingAuthority.isEmpty()) { authorityRepository.save(authority); }

			});

			// stocke les rôles pour l'admin
			Set<Authority> adminAuthorities = new HashSet<>();
			adminAuthorities.add(authorityRepository.findById("ADMIN").orElseThrow());
			adminAuthorities.add(authorityRepository.findById("EDITOR").orElseThrow());
			adminAuthorities.add(authorityRepository.findById("CUSTOMER").orElseThrow());
			// crée un utilisateur avec les rôles ADMIN, EDITOR et CUSTOMER
			User admin = User.builder()
					.username("admin")
					.password(passwordEncoder.encode("admin@pass123"))
					.email("admin@admin.fr")
					.activated(true)
					.authorities(adminAuthorities)
					.build();
			// sauvegarde l'utilisateur s'il n'existe pas déjà
			Optional<User> existingAdmin = Optional.ofNullable(userRepository.findByUsername("admin"));
			if (existingAdmin.isEmpty()) { userRepository.save(admin); }

			// stocke les rôles pour l'éditeur
			Set<Authority> editorAuthorities = new HashSet<>();
			editorAuthorities.add(authorityRepository.findById("EDITOR").orElseThrow());
			editorAuthorities.add(authorityRepository.findById("CUSTOMER").orElseThrow());
			// crée un utilisateur avec les rôles EDITOR et CUSTOMER
			User editor = User.builder()
					.username("editor")
					.password(passwordEncoder.encode("editor@pass123"))
					.email("editor@editor.fr")
					.activated(true)
					.authorities(editorAuthorities)
					.build();
			// sauvegarde l'utilisateur s'il n'existe pas déjà
			Optional<User> existingEditor = Optional.ofNullable(userRepository.findByUsername("editor"));
			if (existingEditor.isEmpty()) { userRepository.save(editor); }
		};
	}
}



