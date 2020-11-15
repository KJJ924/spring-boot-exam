# spring-boot-jpa-study
JPA 기반 웹 애플리케이션 강좌 개발 정리 및 소스코드

강의를 들으면서 이해한 내용을 개인적 견해로 이해하여 정리합니다.
내용적 오류가 존재할수 있습니다.

## 2020-11-15 -1일차
### * 롬복 사용시 주의사항
1. Immutable(불변) 클래스를 제외하고는 아무 파라미터 없는 @EqualsAndHashCode 사용은 금지한다.
2. 일반적으로 비교에서 사용하지 않는 Data 성 객체는 equals & hashCode를 따로 구현하지 않는게 차라리 낫다.
3. 항상 @EqualsAndHashCode(of={“필드명시”}) 형태로 동등성 비교에 필요한 필드를 명시하는 형태로 사용한다.


> 참고 Link: https://kwonnam.pe.kr/wiki/java/lombok/pitfall

- Account 클래스 -> @EqualsAndHashCode(of="id")  사용이유 <br/>
(최소한 꼭 필요하고 일반적으로 변하지 않는 필드에 대해서만 만들도록 노력해야 한다)<br/>
id 는 DB의 Key 값이기 때문에

- @RequiredArgsConstructor -
 fianl 이거나 @NotNull 인 필드값만 생성자로 자동으로 만들어줌  
 -> bean 주입할 때 best practice 는 생성자 주입하는 것 그래서 필드 는 private fianl __ 으로 선언하기 때문에
 @RequiredArgsConstructor 사용 해서 생성자를 만들어도 됨.
 
 ### Validation
- Spring Boot 2.3 이전에는 validation 모듈이 web에 있지만 이후 버전에선 제외 되있음으로
 메이븐에서 의존성을 추가해줘야함
 
-  컨드롤러에서  커맨드 객체에 바인딩할 내용을 검증할때 @Valid @ModelAttribute(생략가능) ExamObject examObject 
 이런식으로 검증을하는데 @Valid 를 선언한 파라미터 뒤에 바로 BindingResult ,Erros 를 파라미터로 작성하여야함.
 
- <strong>Validator 인터페이스<strong>
  - boolean supports(Class<?> aClass) : Validator가 검증할 수 있는 클래스인 지를 판단하는 매서드
  - void validate(Object target, Errors error) : 실제 검증 로직이 이루어지는 메서드
    - > 참고 Link https://engkimbs.tistory.com/728
