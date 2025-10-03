# 🚀 E-commerce MSA Project with Spring Cloud & Kotlin

## 📝 프로젝트 개요 (Overview)

**Spring Cloud**와 **Kotlin**을 이용하여 **MSA(Micro Service Architecture)** 기반의 E-commerce 서버를 개발하는 클론 코딩 프로젝트.

다양한 기술 스택을 경험하고 MSA 환경에서의 여러 문제 해결 능력을 기르는 것을 목표로, 필요에 따라 기존 설계를 변형하고 개선하며 진행했다.

<br>

## 🏗️ 아키텍처 구성도 (Architecture)

<br>

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 |
| --- | --- |
| **Language** | `Kotlin 1.9.25` |
| **Framework** | `Spring Boot 3.3.3` |
| **MSA Common** | `Spring Cloud Gateway`, `Eureka Server`, `Spring Cloud Config` |
| **Database** | `PostgreSQL 15`, `MongoDB 8.0.4` |
| **Message Queue** | `RabbitMQ 4.0`, `Spring Cloud Bus` |
| **CDC Platform** | `Debezium 2.7.3` |
| **API Communication** | `Spring Cloud OpenFeign`, `Resilience4j` (Circuit Breaker) |
| **Monitoring** | `Micrometer`, `Zipkin`, `Prometheus`, `Grafana` |
| **Security** | `Spring Security`, `JWT` |

<br>

## ✨ 주요 기능 및 상세 구현

### 1. Service Discovery & API Gateway

* **Service Registry**: **Spring Cloud Netflix Eureka**를 Service Registry로 사용하여 각 Microservice를 등록하고 관리한다.
* **API Gateway**: **Spring Cloud Gateway**를 도입하여 전체 서비스의 진입점을 단일화했다.
    * Netflix Zuul의 동기/블로킹 방식의 한계를 극복하고, 비동기/논블로킹 처리를 위해 Spring 공식 문서에서 권장하는 Spring Cloud Gateway를 채택했다.
    * Service별 **라우팅 규칙**과 **전역/개별 필터**를 적용하여 인증, 로깅, 경로 재작성 등의 공통 기능을 처리한다.

##### API Gateway 설정 예시 (`application.yml`)
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-m-service
          uri: lb://USER-M-SERVICE
          predicates:
            - Path=/user-m-service/login
            - Method=POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-m-service/(?<segment>.*), /$\{segment}

        - id: user-m-service
          uri: lb://USER-M-SERVICE
          predicates:
            - Path=/user-m-service/users
            - Method=POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-m-service/(?<segment>.*), /$\{segment}

        - id: user-m-service
          uri: lb://USER-M-SERVICE
          predicates:
            - Path=/user-m-service/**
            - Method=GET
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-m-service/(?<segment>.*), /$\{segment}
            - AuthorizationHeaderFilter

        - id: user-m-service
          uri: lb://USER-M-SERVICE
          predicates:
            - Path=/user-m-service/actuator/**
            - Method=GET,POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-m-service/(?<segment>.*), /$\{segment}

        - id: catalog-m-service
          uri: lb://CATALOG-M-SERVICE
          predicates:
            - Path=/catalog-m-service/**

        - id: order-m-service
          uri: lb://ORDER-M-SERVICE
          predicates:
            - Path=/order-m-service/**

        - id: second-service
          uri: lb://SECOND-SERVICE
          predicates:
            - Path=/second-service/**
          filters:
            #                        - AddRequestHeader=second-request, second-request-header2
            #                        - AddResponseHeader=second-response, second-response-header2
            - CustomFilter
            - name: LoggingFilter
              args:
                baseMessage: Hi, there
                preLogger: true
                postLogger: true

      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway GlobalFilter
            preLogger: true
            postLogger: true
```

### 2. 인증 및 인가 (Spring Security & JWT)

* **인증 (Authentication)**: `User-Service`에서 **Spring Security**의 `UsernamePasswordAuthenticationFilter`를 커스터마이징하여 로그인 인증을 처리한다.
* **인가 (Authorization)**: 인증 성공 시 **JWT(JSON Web Token)**를 발급하며, API Gateway의 커스텀 필터(`AuthorizationHeaderFilter`)에서 각 요청의 토큰을 검증하여 서비스 접근 권한을 제어한다.

##### JWT 발급 로직 (`successfulAuthentication`)
```kotlin
class AuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val env: Environment
) : UsernamePasswordAuthenticationFilter() {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val jom = jacksonObjectMapper()

        try {
            val creds = jom.readValue(request.inputStream, RequestLogin::class.java)

            return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.email,
                    creds.password,
                    ArrayList()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException()
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val username = (authResult!!.principal as User).username
        val userDto: UserDto = userService.getUserDetailByEmail(username)

        val key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(env.getProperty("token.secret")!!.toByteArray()))
        val token = Jwts.builder()
            .subject(userDto.userId)
            .expiration(Date(System.currentTimeMillis() + env.getProperty("token.expiration_time")!!.toLong()))
            .signWith(key)
            .compact()

        response!!.addHeader("token", token)
        response.addHeader("userId", userDto.userId)
    }
}
```
### 3. 설정 정보 중앙 관리 및 동기화

* **중앙화**: **Spring Cloud Config**를 사용하여 각 Microservice의 설정 정보(`*.yml`)를 중앙에서 통합 관리한다.
* **암호화**: `JKS` 키스토어를 이용해 DB 접속 정보 등 민감한 설정 값을 암호화하여 보안을 강화했다.
* **동적 refresh**: **Spring Cloud Bus**와 **RabbitMQ**를 연동하여, Config Server의 설정 변경 시 `/actuator/busrefresh` 엔드포인트 호출만으로 모든 서비스에 변경 사항을 실시간으로 전송한다.

---

### 4. 서비스 간 통신 및 장애 관리

* **통신**: **OpenFeign**을 사용하여 MSA 간의 REST API 호출을 인터페이스 기반으로 간결하게 구현했다.
* **장애 격리**: **Resilience4j**를 **Circuit Breaker** 구현체로 사용하여, 특정 서비스의 장애가 다른 서비스로 전파되는 것을 방지한다.
    * 실패율, 응답 지연 시간 등을 기준으로 서킷을 열고(OPEN), 에러를 빠르게 반환하여 시스템 전체의 안정성을 확보했다.
##### OpenFeign 및 Circuit Breaker 적용 예시
```kotlin
// FeignClient Interface
@FeignClient(name = "catalog-m-service")
interface CatalogServiceClient {
    @GetMapping("/catalog-m-service/{productId}/catalog")
    fun getCatalog(@PathVariable productId: String): ResponseCatalog?
}

// Service Logic with Circuit Breaker
@Transactional
override fun createOrder(orderDto: OrderDto): OrderDto {
    ...
    // 서킷 브레이커로 Feign Client 호출을 감쌉니다.
    val catalog = circuitBreaker.run(
        { catalogServiceClient.getCatalog(orderDto.productId) ?: throw BaseException(...) },
        { _: Throwable -> throw BaseException(ErrorCode.OPEN_FEIGN_FAILURE) } // 실패 시 Fallback 로직
    )
    ...
}
```
### 5. 분산 추적 및 모니터링

* **분산 추적**: **Zipkin**과 **Micrometer**를 사용하여 여러 서비스에 걸친 요청 흐름을 **Trace ID** 기반으로 추적하고 시각화한다. 이를 통해 MSA 환경에서의 병목 지점 및 오류 원인 분석을 용이하게 한다.
* **모니터링**: **Prometheus**가 각 서비스의 JVM 메트릭 등 주요 지표를 수집하고, **Grafana** 대시보드를 통해 이를 시각화하여 서비스 상태를 실시간으로 모니터링한다.

<br>

## 💡 추가 개선사항 및 적용 패턴

### 1. CDC & CQRS 패턴 적용

**CQRS(Command and Query Responsibility Segregation)** 패턴을 적용하여 CUD(Command)와 Read(Query)의 책임을 분리하고 데이터베이스 부하를 분산시켰다.

* **Source (Write DB)**: **PostgreSQL**에서 발생하는 데이터 변경(CUD)을 **Debezium**이 WAL(Write-Ahead Log)을 통해 감지한다.
* **Message Queue**: Debezium이 감지한 변경 이벤트를 **RabbitMQ**로 전송한다.
* **Sink (Read DB)**: RabbitMQ의 이벤트를 구독하는 **직접 구현한 Consumer**가 **MongoDB**에 데이터를 동기화하여 조회 성능을 최적화했다.

##### RabbitMQ to MongoDB Consumer 구현
```kotlin
// RabbitMQ Stream에서 Debezium 메시지를 받아 MongoDB에 적용하는 Consumer
messageHandler { context, message ->
    // ... 메시지 파싱 ...
    when (op) {
        "c" -> { // Create
            val doc = Document()
            // ... Bson Document 생성 로직 ...
            collection.insertOne(doc)
        }
        "u" -> { // Update
            val doc = Filters.eq("_id", after!!["id"])
            // ... 업데이트 로직 ...
            collection.updateOne(doc, updateOperation)
        }
        "d" -> { // Delete
            val doc = Filters.eq("_id", before!!["id"])
            collection.deleteOne(doc)
        }
    }
    context.storeOffset() // 오프셋 수동 저장
}
```

### 2. SAGA 패턴을 이용한 분산 트랜잭션

주문 취소와 같이 여러 서비스에 걸친 트랜잭션을 처리하기 위해 **SAGA 패턴 (Choreography-based)** 을 적용했다.

1.  **Order Service**: 주문 취소 요청 시, 주문 상태를 변경하고 `rollback` 이벤트를 **RabbitMQ**에 발행(Produce)한다.
2.  **Catalog Service**: `rollback` 이벤트를 구독(Consume)하여, 주문됐던 상품의 재고를 다시 늘리는 **보상 트랜잭션(Compensating Transaction)** 을 수행한다.
3.  데이터 삭제는 `isValid`와 같은 필드를 이용한 **논리적 삭제(Soft Delete)** 방식을 사용했다.

<br>

## 🚀 컨테이너화 및 실행 (Docker)

모든 Microservice와 인프라(DB, MQ 등)는 **Docker Container**로 실행하여 개발 환경을 표준화하고 배포를 용이하게 했다. `docker run` 명령어의 `-e` 옵션을 통해 yaml 파일을 직접 수정하지 않고 외부에서 설정 값을 주입한다.

##### 서비스 실행 명령어 예시
```shell
# Config Service 실행
docker run -d \
--name config-m-service \
--network ecommerce-network \ 
-p 8888:8888 \
-e "spring.rabbitmq.host=rbmq" \
config-service:1.0

# Discovery Service 실행
docker run -d \
--name discovery-m-service \
--network ecommerce-network \
-p 8761:8761 \
discovery-service:1.0

# User Service 실행
docker run -d \
--name user-m-service \
--network ecommerce-network \
-e "logging.file=/api-logs/users-ws.log" \
user-service:1.0

# Order Service 실행
docker run -d \
--name order-m-service \
--network ecommerce-network \
-e "logging.file=/api-logs/orders-ws.log" \
order-service:1.0

# Catalog Service 실행
docker run -d \
--name catalog-m-service \
--network ecommerce-network \
-e "logging.file=/api-logs/catalogs-ws.log" \
catalog-service:1.0
```