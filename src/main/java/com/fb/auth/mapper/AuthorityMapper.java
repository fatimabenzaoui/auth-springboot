package com.fb.auth.mapper;

import com.fb.auth.dto.AuthorityDTO;
import com.fb.auth.entity.Authority;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper Authority
 */
@Mapper(componentModel = "spring")
public interface AuthorityMapper {
    AuthorityDTO modelToDto(Authority authority);
    List<AuthorityDTO> modelsToDtos(List<Authority> authorities);
    @InheritInverseConfiguration
    Authority dtoToModel(AuthorityDTO authorityDTO);
}
