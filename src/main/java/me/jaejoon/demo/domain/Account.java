package me.jaejoon.demo.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdateByEmail;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    private boolean studyUpdateByWeb = true;

    private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeCheck(){
        this.emailVerified =true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean emailTokenValid(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail(){
        if(this.emailCheckTokenGeneratedAt == null){
            this.emailCheckTokenGeneratedAt = LocalDateTime.now();
            return true;
        }
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));


    }
}
