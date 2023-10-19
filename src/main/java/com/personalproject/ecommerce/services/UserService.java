package com.personalproject.ecommerce.services;

import com.personalproject.ecommerce.dtos.UserDTO;
import com.personalproject.ecommerce.entities.User;
import com.personalproject.ecommerce.exceptions.DatabaseException;
import com.personalproject.ecommerce.exceptions.ResourceNotFoundException;
import com.personalproject.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User result = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));

        return new UserDTO(result);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> result = repository.findAll(pageable);
        return result.map(UserDTO::new);
    }

    @Transactional
    public UserDTO insert(UserDTO dto) {
        User user = new User();
        copyDtoToEntity(dto, user);
        user = repository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = repository.getReferenceById(id);
        copyDtoToEntity(dto, user);
        user = repository.save(user);
        return new UserDTO(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity violation");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBirthDate(dto.getBirthDate());
        entity.setPassword(dto.getPassword());
    }

}
