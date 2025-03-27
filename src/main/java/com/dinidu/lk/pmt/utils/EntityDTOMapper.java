package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.entity.Users;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDTOMapper {

    // Maps a single entity to a DTO
    public static <E, D> D mapEntityToDTO(E entity, Class<D> dtoClass) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            for (Field entityField : entity.getClass().getDeclaredFields()) {
                entityField.setAccessible(true);

                try {
                    Field dtoField = dtoClass.getDeclaredField(entityField.getName());
                    dtoField.setAccessible(true);

                    // Special handling for the 'role' field
                    if (entityField.getName().equals("role") && entity instanceof Users) {
                        Roles roleEntity = (Roles) entityField.get(entity);
                        if (roleEntity != null) {
                            // Convert Roles to UserRole enum
                            UserRole userRole = UserRole.valueOf(roleEntity.getRoleName().toUpperCase().trim());
                            dtoField.set(dto, userRole);
                        } else {
                            dtoField.set(dto, null); // Handle null role
                        }
                    } else {
                        // Default mapping for other fields
                        dtoField.set(dto, entityField.get(entity));
                    }
                } catch (NoSuchFieldException ignored) {
                    // Skip if the field doesn't exist in the DTO
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to DTO", e);
        }
    }

    // Maps a list of entities to a list of DTOs
    public static <E, D> List<D> mapEntityListToDTOList(List<E> entities, Class<D> dtoClass) {
        return entities.stream()
                .map(entity -> mapEntityToDTO(entity, dtoClass))
                .collect(Collectors.toList());
    }

    // Maps a list of DTOs to a list of entities
    public static <E, D> List<E> mapDTOListToEntityList(List<D> dtos, Class<E> entityClass) {
        return dtos.stream()
                .map(dto -> mapDTOToEntity(dto, entityClass))
                .collect(Collectors.toList());
    }

    // Maps a single DTO to an entity
    public static <D, E> E mapDTOToEntity(D dto, Class<E> entityClass) {
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field dtoField : dto.getClass().getDeclaredFields()) {
                dtoField.setAccessible(true);

                try {
                    Field entityField = entityClass.getDeclaredField(dtoField.getName());
                    entityField.setAccessible(true);
                    entityField.set(entity, dtoField.get(dto));
                } catch (NoSuchFieldException ignored) {
                    // Skip if the field doesn't exist in the entity
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping DTO to entity", e);
        }
    }
}
