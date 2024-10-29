package com.kotan4ik.utbotmodule.storage.users;

import com.kotan4ik.utbotmodule.ChatMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {

    default void saveUserChatMode(Long userId, ChatMode mode) {
        User user = findById(userId).orElse(new User(userId, mode)); // Найти пользователя или создать нового
        user.setMode(mode); // Установить новый режим
        save(user); // Сохранить изменения
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = ?1")
    void resetMode(Long userId);

    @Query("SELECT u.mode FROM User u WHERE u.id = ?1")
    Optional<ChatMode> getUserChatMode(Long userId);

}
