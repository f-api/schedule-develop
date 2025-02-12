# Schedule Develop

## API

### Login

<div style="overflow-x: auto;">

| **Method** | **Endpoint** | **Description**      | **Parameters**                  | **Request Body**                                             | **Response**     | **Status Code** |
|------------|--------------|----------------------|---------------------------------|--------------------------------------------------------------|------------------|-----------------|
| `POST`     | `/login`     | 로그인 처리          | 없음                            | `{ "email": string, "password": string }`                    | `"로그인 성공"`  | `200 OK`        |
| `POST`     | `/logout`    | 로그아웃 처리        | 없음                            | 없음                                                         | `"로그아웃 성공"`| `200 OK`        |

</div>

<br>

### User

<div style="overflow-x: auto;">

| **Method** | **Endpoint**    | **Description**      | **Parameters**                                | **Request Body**                                                      | **Response**                                                                                              | **Status Code** |
|------------|-----------------|----------------------|-----------------------------------------------|------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|-----------------|
| `POST`     | `/users/signup` | 회원가입             | 없음                                          | `{ "userName": string, "email": string, "password": string }`          | `{ "id": long, "userName": string, "email": string, "createdAt": string, "updatedAt": string }`             | `200 OK`        |
| `GET`      | `/users`        | 유저 목록 조회       | 없음                                          | 없음                                                                   | `[ { "id": long, "userName": string, "email": string, "createdAt": string, "updatedAt": string }, ... ]`    | `200 OK`        |
| `GET`      | `/users/{id}`   | 단일 유저 조회       | **Path:**<br> - `id` (Long)                    | 없음                                                                   | `{ "id": long, "userName": string, "email": string, "createdAt": string, "updatedAt": string }`             | `200 OK`        |
| `PUT`      | `/users/me`     | 내 정보 수정         | **Session:**<br> - `LOGIN_USER` (Long)         | `{ "userName": string, "email": string, "password": string }`          | `{ "id": long, "userName": string, "email": string, "createdAt": string, "updatedAt": string }`             | `200 OK`        |
| `DELETE`   | `/users/me`     | 회원 탈퇴            | **Session:**<br> - `LOGIN_USER` (Long)         | 없음                                                                   | 없음                                                                                                      | `200 OK`        |

</div>

<br>

### Schedule

<div style="overflow-x: auto;">

| **Method** | **Endpoint**             | **Description**                       | **Parameters**                                                                                                                                                              | **Request Body**                                  | **Response**                                                                                                                                                             | **Status Code** |
|------------|--------------------------|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|
| `POST`     | `/schedules`             | 일정 생성                             | **Session:**<br> - `LOGIN_USER` (Long)                                                                                                                                      | `{ "title": string, "content": string }`          | `{ "id": long, "userId": long, "title": string, "content": string, "createdAt": string, "updatedAt": string }`                                                            | `200 OK`        |
| `GET`      | `/schedules`             | 일정 목록 조회                        | 없음                                                                                                                                                                        | 없음                                              | `[ { "id": long, "userId": long, "title": string, "content": string, "createdAt": string, "updatedAt": string }, ... ]`                                                   | `200 OK`        |
| `GET`      | `/schedules/{id}`        | 단일 일정 조회                        | **Path:**<br> - `id` (Long)                                                                                                                                                  | 없음                                              | `{ "id": long, "userId": long, "title": string, "content": string, "createdAt": string, "updatedAt": string }`                                                            | `200 OK`        |
| `PUT`      | `/schedules/{id}`        | 일정 수정                             | **Path:**<br> - `id` (Long)<br>**Session:**<br> - `LOGIN_USER` (Long)                                                                                                        | `{ "title": string, "content": string }`          | `{ "id": long, "userId": long, "title": string, "content": string, "createdAt": string, "updatedAt": string }`                                                            | `200 OK`        |
| `DELETE`   | `/schedules/{id}`        | 일정 삭제                             | **Path:**<br> - `id` (Long)<br>**Session:**<br> - `LOGIN_USER` (Long)                                                                                                        | 없음                                              | 없음                                                                                                                                                                     | `200 OK`        |
| `GET`      | `/schedules/page`        | 일정 페이지 조회 (페이징 처리)          | **Query:**<br> - `page` (int, default: 1)<br> - `size` (int, default: 10)                                                                                                   | 없음                                              | `Page<[ { "id": long, "title": string, "content": string, "commentCount": int, "createdAt": string, "updatedAt": string, "userName": string } ]>`                            | `200 OK`        |

</div>

<br>

### Comment

<div style="overflow-x: auto;">

| **Method** | **Endpoint**                           | **Description**                            | **Parameters**                                                                                         | **Request Body**             | **Response**                                                                                                      | **Status Code** |
|------------|----------------------------------------|--------------------------------------------|--------------------------------------------------------------------------------------------------------|------------------------------|-------------------------------------------------------------------------------------------------------------------|-----------------|
| `POST`     | `/schedules/{scheduleId}/comments`     | 댓글 생성                                   | **Path:** <br> - `scheduleId`: 일정 식별자 (Long) <br> **Session:** <br> - `LOGIN_USER`: 사용자 ID (Long) | `{ "content": string }`      | `{ "id": long, "userId": long, "scheduleId": long, "content": string, "createdAt": string, "updatedAt": string }`    | `200 OK`        |
| `GET`      | `/schedules/{scheduleId}/comments`     | 특정 일정에 속한 댓글 목록 조회              | **Path:** <br> - `scheduleId`: 일정 식별자 (Long)                                                       | 없음                         | `[ { "id": long, "userId": long, "scheduleId": long, "content": string, "createdAt": string, "updatedAt": string }, ... ]` | `200 OK`        |
| `GET`      | `/comments/{id}`                       | 단일 댓글 조회                             | **Path:** <br> - `id`: 댓글 식별자 (Long)                                                                | 없음                         | `{ "id": long, "userId": long, "scheduleId": long, "content": string, "createdAt": string, "updatedAt": string }`     | `200 OK`        |
| `PUT`      | `/comments/{id}`                       | 댓글 수정                                   | **Path:** <br> - `id`: 댓글 식별자 (Long) <br> **Session:** <br> - `LOGIN_USER`: 사용자 ID (Long)         | `{ "content": string }`      | `{ "id": long, "userId": long, "scheduleId": long, "content": string, "createdAt": string, "updatedAt": string }`     | `200 OK`        |
| `DELETE`   | `/comments/{id}`                       | 댓글 삭제                                   | **Path:** <br> - `id`: 댓글 식별자 (Long) <br> **Session:** <br> - `LOGIN_USER`: 사용자 ID (Long)         | 없음                         | 없음                                                                                                              | `200 OK`        |

</div>

<br>

## ERD

![ERD](./images/erd.png)

## SQL

```sql
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE schedules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  content VARCHAR(255) NOT NULL,
  schedule_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (schedule_id) REFERENCES schedules(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```
