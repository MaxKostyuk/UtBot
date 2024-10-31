package com.kotan4ik.utbotmodule.storage.users;

import com.kotan4ik.utbotmodule.Buttons;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users", schema = "public")
public class User {
    @Id
    @Column(name = "tele_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_mode", columnDefinition = "mode_type")
    private Buttons mode;
}
