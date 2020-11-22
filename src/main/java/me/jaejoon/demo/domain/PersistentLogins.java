package me.jaejoon.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

//    public static final String CREATE_TABLE_SQL = "create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, )";
//token varchar(64) not null, last_used timestamp not null
@Table(name = "persistent_logins" )
@Entity
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;
    @Column(length = 64 ,nullable = false)
    private String username;

    @Column(length = 64,nullable = false)
    private String token;
    @Column(name = "last_used",nullable = false ,length = 64)
    private LocalDateTime lastUsed;

}
