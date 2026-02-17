package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public User transferUserBooks(Long fromUserId, Long toUserId){
        User fromUser = userRepository.findById(fromUserId).orElseThrow(()->new IllegalArgumentException("fromUser unknown"));
        User toUser = userRepository.findById(toUserId).orElseThrow(()->new IllegalArgumentException("toUser unknown"));
        List<Book> books = fromUser.getBooks();
        toUser.setBooks(books);
        fromUser.setBooks(List.of());
        books.stream().forEach(b->b.setUser(toUser));
        return toUser;
    }
    @Transactional
    public User createUser(User user) {
        Optional<User> userfinUser = userRepository.findByEmail(user.getEmail());
        if (userfinUser.isPresent()) {
            return userRepository.save(user);
        }else{
            throw new IllegalArgumentException("Email already exists");
        }
        
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User unknown"));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User userData) {
        User user = userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User unknown"));
        user.setAge(userData.getAge());
        user.setUsername(userData.getUsername());
        user.setEmail(userData.getEmail());
        return userRepository.save(user);

    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User unknown"));
        userRepository.delete(user);

    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByKeyword(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword);
    }

}
