<div align="center">
    <img src="https://github.com/wanted-pre-onboarding-backend-team-s/social-media-integrated-feed-service/assets/50052512/1ba67ff8-8b5c-4937-866c-1a2103718c64" />
</div>

# 자린고비

'자린고비'는 개인 지출 패턴을 분석하고 합리적인 지출 습관을 제안하여, 개인의 재무 관리를 돕고 재무 목표를 달성하는 데 기여하기 위한 서비스입니다.

## Table of Contents

- [주요 기능 및 사용자 스토리](#주요-기능-및-사용자-스토리)
- [기술 스택](#기술-스택)
- [프로젝트 일정 관리](#프로젝트-일정-관리)
- [시스템 아키텍처 설계](#시스템-아키텍처-설계)
- [API 설계](#api-설계)
- [데이터베이스 테이블 설계](#데이터베이스-테이블-설계)
- [테스트](#테스트)

## 주요 기능 및 사용자 스토리

<details>
<summary><b><font size="+1">&nbsp;회원가입/로그인</font></b></summary>

- 고객은 회원가입을 진행해 회원정보를 생성할 수 있다.
- 고객은 로그인해 인증 권한을 획득할 수 있다.
- 고객은 로그아웃해 인증 권한을 파기할 수 있다.
</details>

<details>
<summary><b><font size="+1">&nbsp;기간 별 예산 관리</font></b></summary>

- 고객은 예산을 관리할 기간을 설정하고 예산을 지정해 특정 기간 동안 예산 관리를 시작할 수 있다.
  - 고객은 예산 관리 총액을 제공해 카테고리 별 예산을 추천받을 수 있다.
- 고객은 현재 진행중인 예산 관리 정보를 조회할 수 있다.
- 고객은 특정 예산 관리에 대해 관리 기간을 변경할 수 있다.
- 고객은 특정 예산 관리에 대해 예산을 추가하거나 변경, 삭제할 수 있다.
</details>

<details>
<summary><b><font size="+1">&nbsp;지출 기록</font></b></summary>

- 고객은 자신의 지출 정보를 등록할 수 있다.
- 고객은 자신이 등록한 지출 정보 목록을 조회할 수 있다.
- 고객은 자신이 등록한 특정 지출의 상세 정보를 조회할 수 있다.
- 고객은 자신이 등록한 특정 지출 정보를 수정할 수 있다.
- 고객은 자신이 등록한 특정 지출 정보를 지출 합계에 포함시킬지 결정할 수 있다.
- 고객은 자신이 등록한 지출 정보들을 삭제할 수 있다.
- 고객은 자신이 등록한 특정 지출 정보를 삭제할 수 있다.
</details>

<details>
<summary><b><font size="+1">&nbsp;지출 컨설팅</font></b></summary>

- 고객은 당일 적정 지출금액을 추천받을 수 있다.
</details>

<details>
<summary><b><font size="+1">&nbsp;지출 분석</font></b></summary>

- 고객은 당일 지출 분석 통계자료를 제공받을 수 있다.
- 고객은 현재 예산 관리 기간에 대해 지출 분석 통계자료를 제공받을 수 있다.
</details>

## 기술 스택

[//]: # (TODO: Docker, Redis, Flyway, Swagger 추가)

### 언어 및 Tool

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Framework-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white"/></a>

### 데이터베이스 및 ORM

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/QueryDSL-0078D4?style=for-the-badge&logo=Spring Data JPA&logoColor=white"/></a>

### 테스팅

<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Mockito-087515?style=for-the-badge&logoColor=white"/></a>

### 프로젝트 빌드

<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/></a>

### 프로젝트 진행 및 이슈 관리

<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/></a>
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/></a>
<img src="https://img.shields.io/badge/SourceTree-0052CC?style=for-the-badge&logo=sourcetree&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white"/></a>

## 프로젝트 일정 관리

<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/ea53758f-2729-4b09-948d-fa0d50e15055" />

## 시스템 아키텍처 설계

비즈니스 요구사항 구현을 위한 핵심 비즈니스 로직과, 비즈니스 로직을 처리하기 위한 애플리케이션 내 제반 환경 구성 간의 관심사를 분리하기 위해 Layered
Architecture 및 [Clean Architecture]를 참고하여 아키텍처를 구성했습니다.

[Clean Architecture]: https://www.dandoescode.com/blog/clean-architecture-an-introduction

### 패키지 구조

```text
- common
  - advices
  - config
  - constants
  - converters
  - exceptions
  - filters
  - models
  - response
  - utils
  - validations
  
- domains
  - category
  - customer
  - expense
  - ledger

- session
  - dtos
  - controllers
  - applications
  - entities
  - exceptions
  - repositories
```

```text
- 각 도메인 별 패키지
  - dtos
  - controllers
  - applications
  - models
  - exceptions
  - repositories
```

<details>
<summary><b><font size="+1">&nbsp;구조 상세 설명</font></b></summary>

### Presentation Layer

- 기능을 호출하기 위한 사용자 인터페이스를 정의했습니다.

#### 주요 package

- controllers
- advices

### Application Layer

- 기능의 핵심 비즈니스 로직을 수행하기 위해 필요한 Layer에 요청을 보내고, 결과를 조합하는 동작을 정의했습니다.

#### 주요 package

- applications

### Domain Layer

- 기능의 핵심 비즈니스 로직을 정의했습니다.

#### 주요 package

- models
- entities

### Infrastructure Layer

- 데이터베이스 액세스, 서버 간 통신 등 기능 수행을 지원하기 위한 제반 환경과 관련된 기능을 정의했습니다.

#### 주요 package

- repositories
- utils
- filters
</details>

## API 설계 및 테크 스펙

### 고객: Customer

<table>
  <tr>
    <th>기능</th>
    <th>메서드</th>
    <th>경로</th>
  </tr>
  <tr>
    <td>회원가입</td>
    <td style="text-align: center">POST</td>
    <td>/v1.0/customer/customers</td>
  </tr>
  <tr>
    <td>로그인</td>
    <td style="text-align: center">POST</td>
    <td>/v1.0/customer/sessions</td>
  </tr>
  <tr>
    <td>로그아웃</td>
    <td style="text-align: center">DELETE</td>
    <td>/v1.0/customer/sessions</td>
  </tr>
  <tr>
    <td>액세스 토큰 재발급</td>
    <td style="text-align: center">POST</td>
    <td>/v1.0/customer/access-tokens</td>
  </tr>
  <tr>
    <td>푸시 알림 수신 설정</td>
    <td style="text-align: center">PATCH</td>
    <td>/v1.0/customers/me/push-configuration</td>
  </tr>
</table>

### 카테고리: Category

<table>
  <tr>
    <th>기능</th>
    <th>메서드</th>
    <th>경로</th>
  </tr>
  <tr>
    <td>카테고리 목록 조회</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/categories</td>
  </tr>
</table>

### 예산 관리: Ledger

<table>
  <tr>
    <th>기능</th>
    <th>메서드</th>
    <th>경로</th>
  </tr>
  <tr>
    <td>예산 관리 생성</td>
    <td style="text-align: center">POST</td>
    <td>/v1.0/customer/ledgers</td>
  </tr>
  <tr>
    <td>현재 예산 관리 상세정보 조회</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/ledgers/now</td>
  </tr>
  <tr>
    <td>특정 예산 관리 기간 변경</td>
    <td style="text-align: center">PATCH</td>
    <td>/v1.0/customer/ledgers/{ledger-id}/period</td>
  </tr>
  <tr>
    <td>특정 예산 관리 대상 예산 변경</td>
    <td style="text-align: center">PATCH</td>
    <td>/v1.0/customer/ledgers/{ledger-id}/budgets</td>
  </tr>
  <tr>
    <td><a href="https://docs.google.com/document/d/e/2PACX-1vSpKY1WpYsstYmlncDkVQsed77T1lT6WVSzY81TQ_U1XGOci-O1nU8s_jnDqXYufQTO9V2bSRHvO2F6/pub" style="text-decoration: underline;">카테고리 별 예산 추천</a></td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/ledgers/budget-recommendations</td>
  </tr>
  <tr>
    <td>현재 예산 관리 지출 통계 분석</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/ledgers/{ledger-id}/expense-analysis</td>
  </tr>
</table>

### 지출: Expense

<table>
  <tr>
    <th>기능</th>
    <th>메서드</th>
    <th>경로</th>
  </tr>
  <tr>
    <td>지출 등록</td>
    <td style="text-align: center">POST</td>
    <td>/v1.0/customer/expenses</td>
  </tr>
  <tr>
    <td>지출 목록 조회</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/expenses</td>
  </tr>
  <tr>
    <td>지출 상세정보 조회</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/expenses/{expense-id}</td>
  </tr>
  <tr>
    <td>지출 수정</td>
    <td style="text-align: center">PUT</td>
    <td>/v1.0/customer/expenses/{expense-id}</td>
  </tr>
  <tr>
    <td>지출 지출합계 포함/미포함</td>
    <td style="text-align: center">PATCH</td>
    <td>/v1.0/customer/expenses/included-expense-sum</td>
  </tr>
  <tr>
    <td>지출 여러 건 삭제</td>
    <td style="text-align: center">DELETE</td>
    <td>/v1.0/customer/expenses</td>
  </tr>
  <tr>
    <td>지출 단건 삭제</td>
    <td style="text-align: center">DELETE</td>
    <td>/v1.0/customer/expenses/{expense-id}</td>
  </tr>
  <tr>
    <td>당일 적정 지출금액 추천</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/expenses/recommendations/daily</td>
  </tr>
  <tr>
    <td>당일 지출 통계 분석</td>
    <td style="text-align: center">GET</td>
    <td>/v1.0/customer/expenses/analysis/daily</td>
  </tr>
</table>

## 도메인 설계

<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/a401ad62-deb1-43a6-8fda-235c915563eb" />

## 클래스 다이어그램

<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/1914a651-da70-4a3c-8e29-011cbbd6156c" alt="Customer, CustomerRefreshToken, JwtUtil" style="height: 400px;" />
<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/fce51e75-e92d-4519-9a35-3dc69ab888b5" alt="Expense" style="height: 400px;" />
<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/c7084c39-aea0-4aba-8667-a19b662b43f1" alt="Ledger, Budget, Calculators, Truncators" style="height: 400px;" />

## 데이터베이스 테이블 설계

<img src="https://github.com/hsjkdss228/jaringoby/assets/50052512/ebc6b105-c253-4433-926c-16aba12daaa5" />

## 테스트

### 단위 테스트

- 각 레이어의 테스트 시 다른 레이어에 의존하지 않고 독립적인 비즈니스 로직만을 검증하기 위해 Mocking 개념을 적용한 테스트를 작성했습니다.

<details>
<summary><b><font size="+1">&nbsp;상세 내용 보기</font></b></summary>

### ControllerTest

<img width="452" src="https://github.com/hsjkdss228/jaringoby/assets/50052512/c7bccea7-fd4e-40a9-b9e3-fc2640d2f9d9" />

### ServiceTest

<img width="452" src="https://github.com/hsjkdss228/jaringoby/assets/50052512/48e875a8-e7c2-45bb-b5a3-49cdac1f3408" />

### QueryDslRepositoryImplTest

<img width="452" src="https://github.com/hsjkdss228/jaringoby/assets/50052512/f72e800b-79f9-45de-a2d3-cf0c9cbe9004" />
</details>

<br />
