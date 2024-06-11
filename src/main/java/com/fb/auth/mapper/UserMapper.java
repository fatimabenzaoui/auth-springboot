package com.fb.auth.mapper;

import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

/**
 * Mapper User
 */
@Mapper(componentModel = "spring", uses = {AuthorityMapper.class})
public interface UserMapper {

    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "grantedAuthoritiesCollectionToStringSet")
    UserDTO modelToDto(User user);
    
    List<UserDTO> modelsToDtos(List<User> users);

    @InheritInverseConfiguration
    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "stringSetToAuthoritiesSet")
    User dtoToModel(UserDTO userDTO);

}
