### 예약 테마 전체 조회

**POST /themes**

Response Body

```json
[
  {
    "id": 1,
    "name": "공포 테마",
    "description": "아주 무서운 테마입니다.",
    "thumbnail": "horror.jpg"
  },
  {
    "id": 2,
    "name": "어드벤처 테마",
    "description": "흥미진진한 모험 테마입니다.",
    "thumbnail": "adventure.jpg"
  }
]
```

### 인기 예약 테마 조회

**GET /themes/ranking**

Response Body

```json
[
  {
    "id": 1,
    "name": "공포 테마",
    "description": "아주 무서운 테마입니다.",
    "thumbnail": "horror.jpg"
  },
  {
    "id": 3,
    "name": "판타지 테마",
    "description": "신비로운 판타지 세계입니다.",
    "thumbnail": "fantasy.jpg"
  }
]
```

### 예약 테마 추가 (어드민)

**POST /admin/themes**

Request Body

```json
{
  "name": "새로운 테마",
  "description": "새로운 테마에 대한 설명입니다.",
  "thumbnail": "new_theme.jpg"
}
```

Response Body

```json
{
  "id": 4,
  "name": "새로운 테마",
  "description": "새로운 테마에 대한 설명입니다.",
  "thumbnail": "new_theme.jpg"
}
```

### 예약 테마 삭제 (어드민)

**DELETE /admin/themes/{테마 ID}**
