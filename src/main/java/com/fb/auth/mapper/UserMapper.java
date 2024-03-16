package com.fb.auth.mapper;

import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper User
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO modelToDto(User user);
    List<UserDTO> modelsToDtos(List<User> users);
    @InheritInverseConfiguration
    User dtoToModel(UserDTO userDTO);

}
