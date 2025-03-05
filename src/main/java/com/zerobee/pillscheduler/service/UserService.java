package com.zerobee.pillscheduler.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.zerobee.pillscheduler.dto.AccessTokenBody;
import com.zerobee.pillscheduler.dto.UserDTO;
import com.zerobee.pillscheduler.entity.User;
import com.zerobee.pillscheduler.exception.FirebaseOperationException;
import com.zerobee.pillscheduler.exception.InvalidToken;
import com.zerobee.pillscheduler.exception.TokenNotFound;
import com.zerobee.pillscheduler.exception.UserNotFoundException;
import com.zerobee.pillscheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository repo;
    
    public UserDTO loginUser(AccessTokenBody tokenBody) {
        String authHeader = tokenBody.getToken();
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer")) {
            log.debug("Token Body: {}", tokenBody);
            throw new TokenNotFound();
        }
        
        String token = authHeader.substring(7);
        UserRecord firebaseUser = fetchFirebaseUserFromToken(token).orElseThrow(InvalidToken::new);
        
        if (isUserPresent(firebaseUser))
            return fetchUserById(firebaseUser).toUserDTO();
        else
            return createUser(firebaseUser);
    }
    
    public UserDTO fetchUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            log.debug("Invalid token: {}", token);
            throw new TokenNotFound();
        }
        
        String actualToken = token.substring(7);
        String uid = extractUidFromToken(actualToken);
        
        User user = repo.findById(uid).orElseThrow(InvalidToken::new);
        return user.toUserDTO();
    }
    
    public UserDTO updateUser(String token, UserDTO userDTO) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new TokenNotFound();
        }
        String actualToken = token.substring(7);
        String uid = extractUidFromToken(actualToken);
        
        if (!uid.equals(userDTO.getId())) {
            throw new UserNotFoundException("You can only update your own profile");
        }
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + uid));
        
        existingUser.setName(userDTO.getName());
        existingUser.setPhoto_url(userDTO.getPhoto_url());
        
        User updatedUser = repo.save(existingUser);
        return updatedUser.toUserDTO();
    }
    
    public void deleteUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new TokenNotFound();
        }
        String actualToken = token.substring(7);
        String uid = extractUidFromToken(actualToken);
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + uid));
        
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            repo.delete(existingUser);
        } catch (FirebaseAuthException e) {
            throw new FirebaseOperationException("Failed to delete user from Firebase: " + e.getMessage());
        }
    }
    
    private Optional<UserRecord> fetchFirebaseUserFromToken(String token) {
        String uid = extractUidFromToken(token);
        try {
            return Optional.of(FirebaseAuth.getInstance().getUser(uid));
        } catch (FirebaseAuthException e) {
            log.error("Error fetching Firebase user: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    private String extractUidFromToken(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            log.error("Error decoding token: {}", e.getMessage());
            throw new InvalidToken();
        }
    }
    
    private boolean isUserPresent(UserRecord firebaseUser) {
        return repo.existsById(firebaseUser.getUid());
    }
    
    private User fetchUserById(UserRecord firebaseUser) {
        return repo
                .findById(firebaseUser.getUid())
                .orElseThrow(() -> new UserNotFoundException(firebaseUser.getUid()));
    }
    
    private UserDTO createUser(UserRecord firebaseUser) {
        User user = User
                .builder()
                .id(firebaseUser.getUid())
                .name(firebaseUser.getDisplayName())
                .email(firebaseUser.getEmail())
                .photo_url(firebaseUser.getPhotoUrl())
                .build();
        
        repo.save(user);
        return user.toUserDTO();
    }
}
