# Laundry Booking System - Backend

Полноценный Java backend для системы бронирования стиральных машин, построенный по архитектуре **Boundary-Control-Entity**.

## 🏗 Архитектура

Система следует строгой архитектуре BCE:
- **Boundary** - REST API контроллеры (AuthController, BookingController, ScheduleController, AdminController)
- **Control** - Бизнес-логика (AuthService, BookingService, ScheduleService, AdminService)
- **Entity** - Модели данных (User, Machine, Timeslot, Booking, Schedule)

## 🚀 Технологии

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL 15**
- **JWT Authentication**
- **Docker & Docker Compose**
- **Maven**

## 📦 Структура проекта

```
backend/
├── src/main/java/com/laundry/booking/
│   ├── LaundryBookingApplication.java    # Главный класс приложения
│   ├── entity/                            # Entity слой (модели)
│   │   ├── User.java
│   │   ├── Machine.java
│   │   ├── Timeslot.java
│   │   ├── Booking.java
│   │   └── Schedule.java
│   ├── repository/                        # JPA репозитории
│   │   ├── UserRepository.java
│   │   ├── MachineRepository.java
│   │   ├── TimeslotRepository.java
│   │   ├── BookingRepository.java
│   │   └── ScheduleRepository.java
│   ├── service/                           # Control слой (бизнес-логика)
│   │   ├── AuthService.java
│   │   ├── BookingService.java
│   │   ├── ScheduleService.java
│   │   └── AdminService.java
│   ├── controller/                        # Boundary слой (REST API)
│   │   ├── AuthController.java
│   │   ├── BookingController.java
│   │   ├── ScheduleController.java
│   │   └── AdminController.java
│   ├── dto/                               # Data Transfer Objects
│   ├── config/                            # Конфигурация
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   └── security/                          # JWT утилиты
│       └── JwtUtil.java
├── src/main/resources/
│   ├── application.properties             # Конфигурация приложения
│   ├── schema.sql                         # SQL схема БД
│   └── data.sql                           # Начальные данные
├── Dockerfile                             # Docker образ
└── pom.xml                                # Maven зависимости
```

## 🔧 Установка и запуск

### Предварительные требования

- Docker и Docker Compose установлены
- Порты 8080 (backend) и 5432 (PostgreSQL) свободны

### Запуск через Docker Compose

1. **Перейдите в корневую директорию проекта:**
   ```bash
   cd /Users/q1zin/laundry_appointment_app
   ```

2. **Запустите контейнеры:**
   ```bash
   docker-compose up --build
   ```

   Эта команда:
   - Создаст PostgreSQL контейнер с базой данных
   - Соберет и запустит Java backend
   - Автоматически создаст таблицы и заполнит тестовыми данными

3. **Backend будет доступен на:** `http://localhost:8080`

### Остановка контейнеров

```bash
docker-compose down
```

### Просмотр логов

```bash
docker-compose logs -f backend
```

## 📡 API Endpoints

### Authentication API

#### POST `/api/auth/login`
Авторизация пользователя

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGc...",
  "role": "admin"
}
```

### Booking API

#### POST `/api/bookings/create`
Создание бронирования

**Request:**
```json
{
  "userId": "user-1",
  "machineId": "machine-1",
  "slotId": "slot-1"
}
```

**Response:**
```json
{
  "result": true,
  "message": "Booking created successfully"
}
```

#### POST `/api/bookings/cancel`
Отмена бронирования

**Request:**
```json
{
  "bookingId": "booking-1",
  "userId": "user-1"
}
```

#### POST `/api/bookings/reschedule`
Перенос бронирования

**Request:**
```json
{
  "bookingId": "booking-1",
  "newSlotId": "slot-2",
  "userId": "user-1"
}
```

#### GET `/api/bookings/can-book/{userId}`
Проверка возможности бронирования

#### GET `/api/bookings/slots/available/{machineId}/{slotId}`
Проверка доступности слота

### Schedule API

#### GET `/api/schedule?date=2024-12-17&userId=user-1`
Получение расписания на дату

**Response:**
```json
{
  "machines": [...],
  "timeslots": [...],
  "bookings": [...]
}
```

### Admin API

#### POST `/api/admin/machines/block`
Блокировка машины

**Request:**
```json
{
  "machineId": "machine-1"
}
```

#### POST `/api/admin/machines/unblock`
Разблокировка машины

#### POST `/api/admin/bookings/open`
Открытие бронирования на дату

**Request:**
```json
{
  "date": "2024-12-20"
}
```

#### POST `/api/admin/bookings/close`
Закрытие бронирования на дату

#### DELETE `/api/admin/bookings/{bookingId}`
Удаление бронирования

## 👥 Тестовые пользователи

| Username | Password | Role | Status |
|----------|----------|------|--------|
| admin | password123 | admin | active |
| john_doe | password123 | user | active |
| jane_smith | password123 | user | active |
| blocked_user | password123 | user | blocked |

## 🗄 База данных

### Таблицы:

- **users** - пользователи системы
- **machines** - стиральные машины
- **timeslots** - временные слоты
- **bookings** - бронирования
- **schedules** - расписание работы

### Начальные данные:

- 4 пользователя (включая администратора)
- 4 стиральные машины
- Слоты на 8 дней вперед (08:00-22:00, каждые 2 часа)
- Расписание на 8 дней (первые 3 дня открыты для бронирования)

## 🔐 Безопасность

- Пароли хешируются с использованием BCrypt
- JWT токены для аутентификации
- CORS настроен для frontend приложений
- Spring Security для защиты endpoints

## 🛠 Разработка

### Локальная разработка без Docker

1. Установите PostgreSQL локально
2. Создайте базу данных `laundry_db`
3. Настройте `application.properties` с локальными credentials
4. Запустите приложение:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### Пересборка после изменений

```bash
docker-compose up --build
```

## 📝 Следование спецификации

Реализация **СТРОГО** следует предоставленной инструкции:

✅ Все Entity классы содержат указанные поля и методы  
✅ Все Controller методы реализованы с правильной сигнатурой  
✅ Порядок вызовов в каждом методе соответствует диаграммам  
✅ Все Boundary endpoints созданы и возвращают правильный формат  
✅ Валидация выполняется перед каждой операцией  
✅ Обработка ошибок реализована для всех методов  
✅ Транзакции используются для атомарных операций  
✅ Все состояния используют правильные строковые значения  

## 🐛 Отладка

### Проверка логов backend:
```bash
docker logs laundry-backend
```

### Проверка PostgreSQL:
```bash
docker exec -it laundry-postgres psql -U laundry_user -d laundry_db
```

### Проверка таблиц:
```sql
\dt
SELECT * FROM users;
SELECT * FROM machines;
```

## 📞 Поддержка

При возникновении проблем:
1. Проверьте логи контейнеров: `docker-compose logs`
2. Убедитесь, что порты 8080 и 5432 свободны
3. Проверьте, что Docker daemon запущен
4. Очистите volumes если нужно: `docker-compose down -v`

## 📄 Лицензия

MIT
