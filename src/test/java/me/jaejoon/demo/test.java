package me.jaejoon.demo;

import me.jaejoon.demo.domain.Account;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class test {

    @Test
    void 테스트입니다(){
        Account account = new Account();
        Set<Account> accountS = new HashSet<>();
        accountS.add(account);

        System.out.println(accountS.contains(account));
        account.setId(1L);
        account.setBio("asd");
        System.out.println(accountS.contains(account));
    }
}
