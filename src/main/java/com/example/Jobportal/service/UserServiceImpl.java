package com.example.Jobportal.service;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.User;
import com.example.Jobportal.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class UserServiceImpl implements UserService{
    //    private final UserService userService;
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository)
    {

        this.userRepository=userRepository;
    }


    @Override
    public User saveUser(User user) {

        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(user, entity);

        UserEntity savedEntity = userRepository.save(entity);

        BeanUtils.copyProperties(savedEntity, user);

        return user;
    }



    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntities=userRepository.findAll();
        return userEntities.stream().map(userEntity -> new User(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmailId()
        )).toList();
    }

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity=userRepository.findById(id).orElseThrow();
        User user=new User();
        BeanUtils.copyProperties(userEntity,user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        UserEntity user=userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
        return true;
    }

    @Override
    public User updateUser(Long id, User user) {
        UserEntity userEntity=userRepository.findById(id).orElseThrow();
        userEntity.setEmailId(user.getEmailId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userRepository.save(userEntity);
        return user;
    }
}
