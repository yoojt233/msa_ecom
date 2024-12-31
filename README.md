# Ecommerce-MSA

# 개요

## 프로젝트 소개

### 목표

- Spring Cloud를 이용하여 Micro Service Architecture(이하 MSA) 형태의 E-commerce 서버 개발.
- 인프런 강의를 기반한 클론 코딩으로 필요에 따라 상황에 맞게 변형하며 진행.
- 목적에 맞게 기술 스택을 선정하되 다양한 기술 스택을 경험.

### 구성도

- 현재까지의 구성도.
  ![Web_App_Reference_Architecture](https://github.com/user-attachments/assets/2b9f89fe-1fc2-4f27-b17f-44ea507b8b5b)

**Language**

- _Kotlin 1.9.25_

**Framework**

- _Spring Boot 3.3.3_

**Service Registry**

- _Spring Cloud Netflix Eureka Server 4.1.2_

**API Gateway**

- _Spring Cloud Gateway 4.1.4_

**Microservice**

- User Service
  - _Spring Security 3.3.2_
  - _Spring Cloud OpenFeign 4.1.3_
- Order Service
- Catalog Service

**Config Server**

- _Spring Cloud Config 4.1.3_

**Message Queue Handler**

- _Spring Cloud Bus 4.1.2_

**Docker**

- Database
  - _PostgreSQL 15_
  - _MongoDB 8.0.4_
- Message Queue
  - _RabbitMQ 4.0_
- CDC Platform
  - _Debezium 2.7.3.Final_

# 본론

## 애플리케이션 구성 및 개발

### Client Side Discovery

- Spring Cloud Netflix Eureka Server를 Service Registry로 사용해 Microservice들을 등록한다.
- IP Address:Port로 접속해서 등록된 Service들을 확인할 수 있다. (Ex. localhost:8761)

![Spring_Eureka_Server](https://github.com/user-attachments/assets/463cf3ad-9360-4501-8cb4-b50a3a1d1922)

### API Gateway

- API Gateway로 Spring Cloud Gateway을 사용하였다.
  → Netflix Zuul의 경우 비동기 문제로 maintenance가 되어 개발 및 패치가 중단되었다.
  → 공식 문서 또한 Spring Cloud Gateway를 권장하고 있다.

```yaml
#server
server:
  port: 8000

#eureka
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka

#spring
spring:
  application:
    name: apigw-service
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
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

management:
  endpoints:
    web:
      exposure:
        include: refresh,health,beans,httptrace,busrefresh
```

- Service 별 Routing을 설정해준다.
- Service마다 Filter를 적용할 수 있다.
  → GET Method로 User Service에 전송되는 Request에 AuthorizationHeaderFilter를 적용시켜 인증 작업을 수행도록 한다.
  → RewritePath에 특정한 방식으로 URL을 변경하거나 다른 EndPoint로 Redirection할 수 있다. (RewritePath에 쓰이는 방식은 정규 표현식이 아니라고 한다.)

### 인증 및 인가

```yaml
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

- User Service에 Spring Security를 사용하여 인증, 인가에 대한 기능을 작성하였다.
  → 쉽게 생각해서 인증은 로그인, 인가는 권한.
- `UsernamePasswordAuthenticationFilter`를 상속받은 `AuthenticationFilter`에 의해 로그인을 위한 Request는 Intercept되어 `attemptAuthentication()` 가 실행된다.
  → `attemptAuthentication()` ⇒ `ProvideManager.authentication()` ⇒ `AbstractUserDetailAuthenticationProvider.authentication()`
- 위의 `attempAuthentication()`에 exception 없이 성공하게 되면 `successfulAuthentication()` 로 넘어가게 된다.
  → Spring Cloud Config에서 설정한 값을 사용해 encoding에 사용할 키를 만든다.
  → 키를 사용해 토큰을 생성한다.
- 이후 권한이 필요한 Service의 작업은 API Gateway에서 설정한 `AuthorizationHeaderFilter.apply()` 에서 검사 후 해당 Service로 Request를 보내게 된다.

## CDC 환경 구축 및 구성

### Source

- CDC를 위해 선택한 기술 스택은 다음과 같다.
  - **PostgreSQL** : Microservice 들의 Request를 C, U, D할 Source DB.
    → source DB를 선택한 요구 사항은 트랜잭션 기능을 가진 RDBMS로 대부분의 RDBMS가 가능하다.
    → 많은 DB들 중에서도 트래픽에 따른 Insert, Update, Delete 별 성능, MVCC에 따른 내부 로직 등의 차이가 있겠지만 클론 코딩의 특성 상 그 차이를 실감할 수 없다고 판단하여 평소에 써보지 않았으며 많은 사람들이 사용하는 PostgreSQL을 써보기로 결심했다.
  - **RabbitMQ** : source DB에서 생성된 메시지를 운반할 Message Queue.
    → Message Queue의 선택지는 크게 RabbitMQ와 Kafka로 나뉜다.
    → CDC 구축을 위한 리서치는 대부분 Kafka를 사용하였고 RabbitMQ는 잘 쓰이지 않는 듯 자료도 별로 없다.
    → 하지만 PostgreSQL과 마찬가지로 트래픽이 몰리는 상황은 경험하기 어려울 듯 하여 Kafka에 비해 가벼우며 실시간성을 위한 RabbitMQ Stream 기능을 가진 RabbitMQ를 사용하기로 결정했다.
  - **Debezium** : DB to MQ를 위한 source connector.
    → CDC를 위한 오픈 소스로 log 파일을 관찰해 데이터 변경을 감지하여 이벤트를 발생한다.
    → PostgreSQL의 WAL파일로부터 source하여 RabbitMQ로 sink할 수 있다.
  - **MongoDB** : Microservice 들의 Read 작업이 주가 될 DB.
    → CDC를 통해 Read용, CUD용 DB로 나뉘게 되고, Read 작업이 주가 된다면 RDBMS보다 NoSQL이 적합하다고 판단했다.

### Sink

- PostgreSQL to RabbitMQ를 해주는 source connector는 존재하지만 RabbitMQ to MongoDB를 위한 sink connector는 존재하지 않는다.
  → 정확히는 RabbitMQ를 source로 하는 sink connector가 존재하지 않는다.
- RabbitMQ Stream에 들어오는 Message를 보고 직접 Consumer를 구현했다.
  ```yaml
  class RabbitConsumeImpl : RabbitConsume {
      private val env = Environment.builder().build()
      private val objectMapper = ObjectMapper()

      override fun start(db: String, table: String) {
          println("Wait for connect DB...")
          val targetDB = MongoFactory(username = "esta", password = "zxcv3210").createDB(db)
          val collection = targetDB.getCollection(table)

          val topic = "$db.$table"
          println("Starting Consuming from Queue : $topic")

          val consumer = env.consumerBuilder()
              .stream(topic)
              .offset(OffsetSpecification.next())
              .name("$topic-consumer")
              .manualTrackingStrategy()
              .builder()
              .messageHandler { context, message ->
                  runCatching {
                      objectMapper.readValue(message.bodyAsBinary, object : TypeReference<Map<String, Any>>() {})
                  }.onSuccess {
                      val payload = it["payload"] as Map<String, Any>
                      val before = payload["before"] as Map<String, Any>?
                      val after = payload["after"] as Map<String, Any>?
                      val op = payload["op"] as String?
                      val transaction = payload["transaction"] as String?

                      when (op) {
                          "c" -> {
                              val doc = Document()
                              for (key in after!!.keys) {
                                  if (key == "id") doc.append("_id", after[key] as Int) else doc.append(key, after[key])
                              }

                              collection.insertOne(doc)
                          }

                          "u" -> {
                              val doc = Filters.eq("_id", after!!["id"])
                              val updateList = after!!.map { (key, value) ->
                                  Updates.set(if (key == "id") "_id" else key, value)
                              }
                              val updateOperation = Updates.combine(updateList)

                              collection.updateOne(doc, updateOperation)
                          }

                          "d" -> {
                              val doc = Filters.eq("_id", before!!["id"])

                              collection.deleteOne(doc)
                          }

                          else -> println("Check Rabbitmq message IMMEDIATELY!")
                      }

                      context.storeOffset()
                  }.onFailure { e ->
                      e.printStackTrace()
                  }
              }
              .build()
      }

      override fun close() {
          env.close()
      }
  }
  ```
  → 추후 추가 작업 필요.
- 아래 페이지에는 각 구성에 대한 설치 및 실행, 진행하면서 겪은 Trouble Shooting이 적혀있다.
  [PostgreSQL:15](https://www.notion.so/PostgreSQL-15-1677c11afefe806b995bc1f51bd9446e?pvs=21)
  [RabbitMQ:4.0-management](https://www.notion.so/RabbitMQ-4-0-management-1677c11afefe80ffb716e34d0ddd3a60?pvs=21)
  [Debezium/server:2.7.3.Final](https://www.notion.so/Debezium-server-2-7-3-Final-16a7c11afefe80ecaec4c1464a253fbb?pvs=21)
  [MongoDB:8.0.4](https://www.notion.so/MongoDB-8-0-4-16c7c11afefe800abfbaf3502137b1cd?pvs=21)

# 참고
