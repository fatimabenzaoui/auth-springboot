package com.fb.auth.mapper;

import com.fb.auth.dto.AuthorityDTO;
import com.fb.auth.entity.Authority;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper Authority
 */
@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    AuthorityDTO modelToDto(Authority authority);
    List<AuthorityDTO> modelsToDtos(List<Authority> authorities);
    @InheritInverseConfiguration
    Authority dtoToModel(AuthorityDTO authorityDTO);

    /**
     * Convertit une collection d'objets GrantedAuthority en un ensemble de chaînes de caractères
     * 
     * @param authorities Collection d'objets GrantedAuthority (peut être null)
     * @return Un ensemble de chaînes de caractères représentant les autorités mais i la collection d'entrée est null, un ensemble vide est retourné
     */
    @Named("grantedAuthoritiesCollectionToStringSet")
    default Set<String> authoritiesToStringSet(Collection<? extends GrantedAuthority> authorities) {
        // vérifie si la collection d'authorities est null et retourne un ensemble vide si c'est le cas
        if (authorities == null) {
            return Collections.emptySet();
        }
        // transforme chaque GrantedAuthority en une chaîne de caractères (leur nom d'autorité) et les collecte dans un ensemble
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }

    /**
     * Convertit un ensemble de chaînes de caractères en un ensemble d'objets Authority
     * 
     * @param strings Ensemble de chaînes de caractères représentant les libellés des autorités (peut être null)
     * @return Un ensemble d'objets Authority mais si l'ensemble d'entrée est null, un ensemble vide est retourné
     */
    @Named("stringSetToAuthoritiesSet")
    default Set<Authority> stringSetToAuthorities(Set<String> strings) {
        // vérifie si l'ensemble de chaînes de caractères est null et retourne un ensemble vide si c'est le cas
        if (strings == null) {
            return Collections.emptySet();
        }
        // pour chaque chaîne de caractères, crée un nouvel objet Authority, définit son libellé d'autorité et le collecte dans un ensemble
        return strings.stream()
            .map(str -> {
                Authority authority = new Authority();
                authority.setAuthorityLabel(str);
                return authority;
            })
            .collect(Collectors.toSet());
    }
}
