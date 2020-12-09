package me.jaejoon.demo.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jaejoon.demo.WithAccountAndStudyPage;
import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Study;
import me.jaejoon.demo.domain.Zone;
import me.jaejoon.demo.form.ZoneForm;
import me.jaejoon.demo.zone.ZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudySettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    StudyService studyService;

    @Test
    @DisplayName("스터디 소개 설정 페이지 보기")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void viewDescriptionSetting() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(get("/study/"+ path +"/settings/description"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @DisplayName("스터디 소개 변경")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void editDescription() throws Exception{
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);
        mockMvc.perform(post("/study/"+ path +"/settings/description")
                .param("shortDescription","shortDescription 변경됨")
                .param("fullDescription","fullDescription 변경됨")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+ path +"/settings/description"))
                .andExpect(flash().attributeExists("message"));

        Study study = studyRepository.findByPath(path);
        assertThat(study.getShortDescription()).isEqualTo("shortDescription 변경됨");
        assertThat(study.getFullDescription()).isEqualTo("fullDescription 변경됨");
    }

    @Test
    @DisplayName("배너 설정 페이지 보기")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void showStudySettingBannerPage() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(get("/study/"+ path +"/settings/banner"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("study/banner"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("배너 이미지 변경 ")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void StudySettingBannerEdit() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(post("/study/"+ path +"/settings/banner")
                .param("image","ImageDataURL")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/"+ path +"/settings/banner"));


        Study study = studyRepository.findByPath(path);

        assertThat(study.getImage()).isEqualTo("ImageDataURL");
    }

    @Test
    @DisplayName("배너 이미지 on ")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void onBanner() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(post("/study/"+ path +"/settings/banner/enable")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/"+ path +"/settings/banner"));

        Study study = studyRepository.findByPath(path);
        assertThat(study.isUseBanner()).isTrue();
    }

    @Test
    @DisplayName("배너 이미지 off ")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void offBanner() throws Exception {
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(post("/study/"+ path +"/settings/banner/disable")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/"+ path +"/settings/banner"));

        Study study = studyRepository.findByPath(path);
        assertThat(study.isUseBanner()).isFalse();
    }

    @Test
    @DisplayName("zone 설정 페이지 보기")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void viewZoneSettingPage() throws Exception{
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(get("/study/"+ path +"/settings/zones"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/zones"));
    }

    @Test
    @DisplayName("zone 추가")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void addZones() throws Exception{
        Zone testZone = Zone.builder().city("Asan").localNameOfCity("아산시").province("South Chungcheong").build();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        mockMvc.perform(post("/study/"+ path +"/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findByPath(path);
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());

        assertThat(study.getZones().contains(zone)).isTrue();
    }

    @Test
    @DisplayName("zone 삭제")
    @WithAccountAndStudyPage(value ="kjj924",title ="봄싹스터디",path = "test")
    void removeZones() throws Exception{
        Zone testZone = Zone.builder().city("Asan").localNameOfCity("아산시").province("South Chungcheong").build();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());
        String path = URLEncoder.encode("test", StandardCharsets.UTF_8);

        Study study = studyRepository.findByPath(path);
        mockMvc.perform(post("/study/"+ path +"/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertThat(study.getZones().contains(zone)).isFalse();
    }

}