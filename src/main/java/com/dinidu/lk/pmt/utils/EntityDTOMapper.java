package com.dinidu.lk.pmt.utils;

import com.dinidu.lk.pmt.entity.Roles;
import com.dinidu.lk.pmt.entity.Users;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
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

                    // Handle 'role' field (if applicable)
                    if (entityField.getName().equals("role") && entity instanceof Users) {
                        Roles roleEntity = (Roles) entityField.get(entity);
                        if (roleEntity != null) {
                            UserRole userRole = UserRole.valueOf(roleEntity.getRoleName().toUpperCase().trim());
                            dtoField.set(dto, userRole);
                        } else {
                            dtoField.set(dto, null);
                        }
                    } else {
                        // Special handling for JavaFX properties (e.g., StringProperty, ObjectProperty)
                        if (dtoField.getType().equals(StringProperty.class)) {
                            StringProperty stringProperty = (StringProperty) dtoField.get(dto);
                            stringProperty.set((String) entityField.get(entity));
                        } else if (dtoField.getType().equals(ObjectProperty.class)) {
                            // Handle ObjectProperty with proper type casting
                            ObjectProperty<Object> objectProperty = (ObjectProperty<Object>) dtoField.get(dto);

                            // Get the type of the ObjectProperty from the field type
                            Class<?> fieldType = (Class<?>) ((java.lang.reflect.ParameterizedType) dtoField.getGenericType()).getActualTypeArguments()[0];

                            // Safely cast the value to the correct type for ObjectProperty
                            Object value = entityField.get(entity);
                            if (value != null) {
                                // Type-safe casting: cast the value to the correct field type
                                objectProperty.set(fieldType.cast(value));
                            } else {
                                objectProperty.set(null);
                            }
                        } else {
                            // Default mapping for other fields
                            dtoField.set(dto, entityField.get(entity));
                        }
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

    // Maps a single DTO to an entity
    public static <D, E> E mapDTOToEntity(D dto, Class<E> entityClass) {
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field dtoField : dto.getClass().getDeclaredFields()) {
                dtoField.setAccessible(true);
                try {
                    Field entityField = entityClass.getDeclaredField(dtoField.getName());
                    entityField.setAccessible(true);
                    Object dtoValue = dtoField.get(dto);
                    if (dtoValue instanceof javafx.beans.value.ObservableValue) {
                        // If it's a JavaFX property (e.g., StringProperty, ObjectProperty), get the value from it
                        Object realValue = ((javafx.beans.value.ObservableValue<?>) dtoValue).getValue();
                        entityField.set(entity, realValue);
                    } else {
                        entityField.set(entity, dtoValue);
                    }
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
