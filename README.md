# spring-boot-jpa-study
JPA 기반 웹 애플리케이션 강좌 개발 정리 및 소스코드


## 2020-11-15 -1일차
### * 롬복 사용시 주의사항
1. Immutable(불변) 클래스를 제외하고는 아무 파라미터 없는 @EqualsAndHashCode 사용은 금지한다.
2. 일반적으로 비교에서 사용하지 않는 Data 성 객체는 equals & hashCode를 따로 구현하지 않는게 차라리 낫다.
3. 항상 @EqualsAndHashCode(of={“필드명시”}) 형태로 동등성 비교에 필요한 필드를 명시하는 형태로 사용한다.


> 참고 Link: https://kwonnam.pe.kr/wiki/java/lombok/pitfall

Account 클래스 -> @EqualsAndHashCode(of="id")  사용이유 <br/>
(최소한 꼭 필요하고 일반적으로 변하지 않는 필드에 대해서만 만들도록 노력해야 한다)<br/>
id 는 DB의 Key 값이기 때문에
