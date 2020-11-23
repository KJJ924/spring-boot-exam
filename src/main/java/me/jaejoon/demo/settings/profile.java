package me.jaejoon.demo.settings;

import lombok.Data;
import me.jaejoon.demo.domain.Account;

@Data
public class profile {

    private String bio;

    private String url;

    private String occupation;

    private String location;

    public profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location =account.getLocation();
    }
}
