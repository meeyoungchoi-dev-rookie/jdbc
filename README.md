# SPRING DB 1편

## 계좌이체 예제를 진행하면서 발생했던 에러와 해결과정
### 계좌이체 비즈니스 로직에서 예외가 발생하는 경우 트랜잭션이 롤백된다
#### 에러가 발생한 상황
+ 계좌이체중 예외가 발생하여 비즈니스 로직이 롤백되었다
+ 데이터가 트랜잭션 시작되기 전으로 롤백이 되었는지 검증하는 테스트에서 에러가 발생했다
+ 즉 , 데이터가 롤백되지 않아 테스트가 통과되지 못했다
### 에러가 발생한 원인
+ 비즈니스 로직과 리파지터리가 하나의 트랜잭션으로 묶여 있다
+ 트랜잭션이 시작되려면 커넥션이 필요하다
+ 비즈니스 로직과 연결된 리파지터리 메서드에서도 동일한 커넥션이 유지되도록 커넥션을 파라미터로 전달해주지 않았다
+ 따라서 리파지터리의 메서드에서 실행되는 작업은 커넥션이 달라 데이터가 롤백되지 못했다
### 에러 해결 과정
+ 비즈니스 로직과 관련된 리파지터리의 메서드가 호출될떄 커넥션 객체도 같이 전달하여 동일한 커넥션에서 DB 작업이 이뤄지도록 코드를 변경해줬다
+ 이를 통해 비즈니스 로직에서 롤백이 일어나면 데이터가 트랜잭션 시작전으로 올바르게 롤백될 수 있어 테스트를 통과했다

## JDBC 등장 배경
- 과거에는 DB마다 커넥션을 연결하는 방법 , SQL을 전달하는 방법 , 응답받는 방법이 전부 달랐다
- DB의 종류 또한 정말 많다 (MySQL, OracleDB, h2 ...)
- 어떤 DB를 사용하는지에 따라 연결방법을 매번 배워야 했다
- 번거로움을 해결하기위해 JDBC 표준 인터페이스가 등장했다

## JDBC 표준 인터페이스
- DBMS와 Connection을 연결하기 위한 인터페이스, SQL을 담을 Satement ,SQL 요청 응답을 담을 ResultSet 인터페이스를 표준으로 정의하여 제공한다
- 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API
- 다양한 종류의 관계형 데이터베이스에 접속하고 SQL문을 수행하여 처리하고자 할 때 사용되는 표준  SQL 인터페이스 API이다
- 사용하는 DBMS 서버에 맞는 JDBC 드라이버가 필요하다
- `JDBC 드라이버` - java.sql의 인터페이스를 상속하여 메서드 내부를 구현한 클래스 파일

### JDBC를 사용했을때 코드

- CRUD 작업을 진행할 때마다 데이터베이스와 연결하는 Connection 객체를 항상 새로 연결해 줘야 한다
- SQL문을 전달하기 위한 PreparedStatement 객체에 매개변수를 세팅해 줘야 한다
- SQL문을 실행해주는 메서드가 반복적으로 사용된다
- 커넥션, PreparedStatement , ResultSet을 닫아줘야 한다

### SQL Mapper
- JDBC를 편리하게 사용할수 있도록 도와준다
- JDBC를 사용했을때는 CRUD를 할때 마다 커넥션을 연결하고 쿼리를 날리고 결과를 받고 자원을 해제하는 코드가 중복됬다
- SQL Mapper가 JDBC의 반복 코드를 제거해 준다


### ORM

- 객체를 관계형 데이터베이스 테이블과 매핑해 준다
- 반복적인 SQL을 작성하지 않아도 된다
- 각 데이터베이스마다 다른 SQL을 사용하는 문제를 해결해 준다
- 예) JPA , 하이버네이트, 이클립스 링크


### SQL Mapper 와 ORM 차이

- `SQL Mapper`  - SQL 쿼리 실행 결과를 객체에 바인딩해준다
    - SQL 의존적인 방법
- `MyBatis` - SQL을 xml 파일로 분리하여 관리
    - SQL 쿼리 결과를 객체에 매핑할수 있도록 도와준다
    - 동적쿼리를 지원한다
- 객체와 관계형 테이블 간 패러다임 불일치 문제가 발생할 수 있다

- `ORM`
- 객체와 DB 테이블을 매핑하여 데이터를 객체화 해준다
- CRUD 관련 메서드를 사용하면 자동으로 SQL이 만들어 진다
- DBMS에 종속적이지 않다
- 복잡한 쿼리의 경우 JPQL 또는 SQL Mapper를 혼용하여 사용할 수 있다

## 트랜잭션 개념

- 하나의 거래를 안전하게 처리하도록 보장해주는 것
- 여러 작업을 진행하다가 문제가 생겼ㄷ을 경우 이전 상태로 롤백하기 위해 사용
- 한번 질의가 실행되면 질의가 모두 수행되거나 모두 수행되지 않는 작업수행의 논리적 단위
- 트랜잭션 커밋 - 작업이 마무리 됨
- 트랜잭션 롤백 - 작업을 취소하고 이전 상태로 돌림

## 트랜잭션 ACID

- 데이터베이스 내에서 일어나는 하나의 트랜잭션의 안전성을 보장하기 위해 필요한 성질
- 원자성, 일관성, 격리성, 지속성을 보장해 주는 것
- 원자성 - 트랜잭션 내에서 실행한 작업은 마치 하나의 작업인 것처럼 모두 성공하거나 모두 실패해야 한다
  - 트랜잭션의 작업이 부분적으로 실행되거나 중단되지 않는 것을 보장
- 일관성 - 트랜잭션이 성공적으로 완료되면 일관적인 DB 상태를 유지한다
- 격리성 - 동시에 실행되는 트랜잭션이 서로 영향을 미치지 않도록 격리해야 한다
  - 동시에 같은 데이터를 수정하지 못하도록 한다
  - 격리성은 동시성과 관련있어 성능이슈 관련하여 격리수준을 선택해야 한다
- 지속성 - 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다
  - 중간에 시스템에 문제가 발생해도 데이터베이스 로그를 사용하여 성공한 트랜잭션 내용을 복구 해야 한다


### 트랜잭션 예제2 - 테스트

```java
@Test
@DisplayName("정상 이체")
void accountTransfer() throws SQLException {
    // given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    // when
    log.info("START TX");
    memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
    log.info("END TX");

    // then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberB.getMemberId());

    assertThat(findMemberA.getMoney()).isEqualTo(8000);
    assertThat(findMemberB.getMoney()).isEqualTo(12000);
}
```


- 이체중 예외가 발생하는 코드 테스트
- 트랜잭션이 시작되고 비즈니스 로직에서 예외가 터진다
- 예외가 터지면 트랜잭션을 롤백 시킨다
- 그런면 데이터가 트랜잭션이 시작되기 전으로 원복된다
- 따라서 데이터를 검증할 때 원복된 값을 기준으로 비교해야 올바른 테스트 결과를 얻을 수 있다
- 비즈니스 로직 안에서는 하나의 트랜잭션으로 묶여 동작하기 때문에 예외가 발생하는 경우 데이터가 롤백된다

```java
@Test
@DisplayName("이체중 예외가 발생함")
void accountTransferEx() throws SQLException {
    // given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberEX = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberEX);

    // when
    assertThatThrownBy(() -> memberService.accountTransfer(
            memberA.getMemberId(), memberEX.getMemberId(), 2000))
            .isInstanceOf(IllegalStateException.class);

    // then

    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberEX.getMemberId());
    assertThat(findMemberA.getMoney()).isEqualTo(10000);// 트랜잭션을 롤백 했기 떄문에 memberA의 금액도 트랜잭션 시작전으로 롤백 되어야 한다
    assertThat(findMemberB.getMoney()).isEqualTo(10000);
}
```

```java
public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            // 트랜잭션 시작
            con.setAutoCommit(false);
            // 비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); // 성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }
```

- 비즈니스 로직이 하나의 트랜잭션으로 묶여 있다
- 따라서 toMember를 검증하는 과정에서 예외가 발생하는 경우
- fromId가 갖고 있던 money도 트랜잭션이 시작되기 전으로 데이터가 원복되고
- toId가 갖고 있던 money도 트랜잭션이 시작되기 전으로 데이터가 원복된다

```java
private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
  Member fromMember = memberRepository.findById(con, fromId);

  Member toMember = memberRepository.findById(con, toId);

  memberRepository.update(con, fromId, fromMember.getMoney() - money);
  validation(toMember);
  memberRepository.update(con, toId, toMember.getMoney() + money);
}
```

- 따라서 memberA의 금액을 검증할때 트랜잭션이 시작되기 이전 데이터를 기준으로 검증해야 한다

```java
// then
Member findMemberA = memberRepository.findById(memberA.getMemberId());
Member findMemberB = memberRepository.findById(memberEX.getMemberId());
assertThat(findMemberA.getMoney()).isEqualTo(10000);// 트랜잭션을 롤백 했기 떄문에 memberA의 금액도 트랜잭션 시작전으로 롤백 되어야 한다
assertThat(findMemberB.getMoney()).isEqualTo(10000);

```

### 정리

- 트랜잭션은 비즈니스 로직에서 시작되고 종료되어야 한다
- 비즈니스 로직을 하나의 트랜잭션으로 관리하면 예외가 발생했을때 비즈니스 로직전체를 롤백시킬수 있기 때문이다
- 트랜잭션내에서 디비 서버에 왔다갔다 할때 커넥션을 파라미터로 전달하여 동일한 커넥션을 통해 DB 작업이 이루어지게 한다
- autocommit 모드를 false로 설정하여 트랜잭션을 시작시킨다
- 커넥션 풀을 사용하는 경우 커넥션을 해제할 때 autocommit 모드를 true로 설정한 후 커넥션 풀로 반환해 준다