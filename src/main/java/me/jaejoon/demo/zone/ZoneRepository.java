package me.jaejoon.demo.zone;

import me.jaejoon.demo.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ZoneRepository extends JpaRepository<Zone,Long> {
    Zone findByCityAndProvince(String cityName, String provinceName);
}
