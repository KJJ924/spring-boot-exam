package me.jaejoon.demo.tag;

import lombok.RequiredArgsConstructor;
import me.jaejoon.demo.domain.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String title){
        Tag byTitle = tagRepository.findByTitle(title);

        if(byTitle ==null){
            byTitle = tagRepository.save(Tag.builder().title(title).build());
        }
        return byTitle;
    }
}
