package com.fb.auth;

import com.fb.auth.config.JwtFilter;
import com.fb.auth.config.SecurityConfig;
import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.ProfilePhotoRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.ProfilePhoto;
import com.fb.auth.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	// ONLY FOR TESTING ROLES AND PERMISSIONS
//	@Bean
//	CommandLineRunner start(
//			AuthorityRepository authorityRepository,
//			UserRepository userRepository,
//			BCryptPasswordEncoder bCryptPasswordEncoder,
//			ProfilePhotoRepository profilePhotoRepository
//	) {
//		return args -> {
//
//			// crée les rôles
//			Stream.of("ADMIN", "EDITOR", "CUSTOMER").forEach(r -> {
//				Authority authority = new Authority();
//				authority.setAuthorityLabel(r);
//				// sauvegarde les rôles s'ils n'existent pas déjà
//				Optional<Authority> existingAuthority = authorityRepository.findById(r);
//				if (existingAuthority.isEmpty()) { authorityRepository.save(authority); }
//			});
//
//			// charge la photo de profil par défaut
//			ProfilePhoto defaultProfilePhoto = profilePhotoRepository.findByFileName("default-profile-photo.jpg");
//			if (defaultProfilePhoto == null) {
//				throw new RuntimeException("Default profile photo 'default-profile-photo.jpg' not found in the database.");
//			}
//
//			// stocke les rôles pour l'admin
//			Set<Authority> adminAuthorities = new HashSet<>();
//			adminAuthorities.add(authorityRepository.findById("ADMIN").orElseThrow());
//			adminAuthorities.add(authorityRepository.findById("EDITOR").orElseThrow());
//			adminAuthorities.add(authorityRepository.findById("CUSTOMER").orElseThrow());
//			// crée un utilisateur avec les rôles ADMIN, EDITOR et CUSTOMER
//			User admin = User.builder()
//					.username("admin")
//					.password(bCryptPasswordEncoder.encode("admin@pass123"))
//					.email("admin@admin.fr")
//					.activated(true)
//					.authorities(adminAuthorities)
//					.profilePhoto(defaultProfilePhoto)
//					.build();
//			// sauvegarde l'utilisateur s'il n'existe pas déjà
//			Optional<User> existingAdmin = Optional.ofNullable(userRepository.findByUsername("admin"));
//			if (existingAdmin.isEmpty()) { userRepository.save(admin); }
//
//			// stocke les rôles pour l'éditeur
//			Set<Authority> editorAuthorities = new HashSet<>();
//			editorAuthorities.add(authorityRepository.findById("EDITOR").orElseThrow());
//			editorAuthorities.add(authorityRepository.findById("CUSTOMER").orElseThrow());
//			// crée un utilisateur avec les rôles EDITOR et CUSTOMER
//			User editor = User.builder()
//					.username("editor")
//					.password(bCryptPasswordEncoder.encode("editor@pass123"))
//					.email("editor@editor.fr")
//					.activated(true)
//					.authorities(editorAuthorities)
//					.profilePhoto(defaultProfilePhoto)
//					.build();
//			// sauvegarde l'utilisateur s'il n'existe pas déjà
//			Optional<User> existingEditor = Optional.ofNullable(userRepository.findByUsername("editor"));
//			if (existingEditor.isEmpty()) { userRepository.save(editor); }
//		};
//	}
}



