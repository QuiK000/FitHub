# FitHub — Fitness Studio Management Platform

<div style="text-align: center;">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?style=flat-square&logo=react)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue?style=flat-square&logo=typescript)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-blue?style=flat-square&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-latest-red?style=flat-square&logo=redis)
![Docker](https://img.shields.io/badge/Docker-compose-blue?style=flat-square&logo=docker)

</div>

---

## 🇬🇧 English

### Overview

FitHub is a full-stack fitness studio management platform designed for gyms, personal trainers, and their clients. It provides a centralized command center for orchestrating workouts, nutrition plans, memberships, live check-ins, and performance analytics — all in one dashboard.

### ✨ Features

| Module | Description |
|---|---|
| **Authentication** | JWT (RSA-signed), email verification, password reset, token blacklisting, rate limiting |
| **User Profiles** | Separate trainer and client profiles with full CRUD |
| **Workout Plans** | Create, assign, and track structured training programs with daily exercise scheduling |
| **Workout Logs** | Log individual exercise sessions with sets, reps, weight, duration, and difficulty |
| **Training Sessions** | Schedule group/personal sessions, client join flow, trainer check-in with membership validation |
| **Memberships** | Monthly, yearly, or visit-based memberships with freeze/unfreeze/extend/cancel lifecycle |
| **Payments** | Payment processing with optional TRON cryptocurrency validation |
| **Nutrition** | Meal plans, food database, water intake tracking with daily/weekly analytics |
| **Progress Tracking** | Body measurements, fitness goals, personal records, progress photos |
| **Notifications** | In-app notifications, email (async), Telegram bot integration |
| **Reviews** | Client reviews for trainers with moderation, rating aggregation |
| **Dashboard Analytics** | Active clients, revenue, daily check-ins, popular sessions, attendance charts |
| **File Storage** | MinIO-based video and image uploads with range request support |
| **Caching** | Multi-layer Redis caching with granular TTL per entity type |
| **Monitoring** | Prometheus metrics + Grafana dashboards, HikariCP pool monitoring |

### 🏗️ Architecture

```
fithub/
├── backend/                      # Spring Boot application
│   └── src/main/java/
│       └── com/dev/quikkkk/
│           ├── core/             # Shared: config, security, exceptions, DTOs
│           └── modules/
│               ├── auth/         # Authentication & token management
│               ├── user/         # Profiles, roles, specializations
│               ├── workout/      # Plans, exercises, sessions, logs, attendance
│               ├── membership/   # Memberships, payments
│               ├── nutrition/    # Meals, foods, water intake
│               ├── progress/     # Body measurements, goals, PRs, photos
│               ├── notification/ # Email, in-app, Telegram
│               ├── review/       # Trainer reviews
│               ├── dashboard/    # Analytics
│               └── storage/      # File upload/serving
└── frontend/                     # React + TypeScript SPA
    └── src/
        ├── pages/                # Route-level components
        ├── components/           # Reusable UI components
        ├── services/             # Axios API layer
        ├── store/                # Zustand state management
        └── layouts/              # App shell layouts
```

### 🛠️ Tech Stack

**Backend**
- Java 21 · Spring Boot 4.0.3 · Spring Security · Spring Data JPA
- PostgreSQL · Redis · MinIO
- JWT (RSA asymmetric keys) · Flyway migrations
- Prometheus + Grafana · Docker Compose

**Frontend**
- React 19 · TypeScript 5.9 · Vite 7
- Tailwind CSS 3 · Framer Motion
- Zustand · React Router 7 · Recharts · Axios

### 🚀 Quick Start

**Prerequisites:** Java 21, Node.js 20+, Docker

```bash
# 1. Clone the repository
git clone https://github.com/your-username/fithub.git
cd fithub

# 2. Start infrastructure (PostgreSQL, Redis, MinIO, MailDev)
cd docker
docker compose -f docker-compose-dev.yml up -d

# 3. Configure environment
cp env.example env.properties
# Edit env.properties with your values

# 4. Run backend
./mvnw spring-boot:run

# 5. Run frontend
cd frontend
npm install
npm run dev
```

Backend runs at `http://localhost:8080`, frontend at `http://localhost:5173`.  
API docs: `http://localhost:8080/swagger-ui.html`

### ⚙️ Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `REDIS_HOST` | Redis host |
| `REDIS_PASSWORD` | Redis password |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token TTL in ms |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token TTL in ms |
| `FRONTEND_URL` | Frontend origin for CORS & email links |
| `TELEGRAM_TOKEN` | Telegram bot token (optional) |

### 🔐 Security

- RSA asymmetric key pair for JWT signing/verification
- Token blacklisting via Redis (logout invalidation)
- Per-IP login rate limiting
- Email domain blocklist (disposable email prevention)
- `@PreAuthorize` role-based access on every endpoint
- Distributed session locking (Redis) for concurrent check-in safety

### 📦 Deployment

```bash
cd docker
docker compose build --no-cache backend
docker compose up -d
```

CI/CD is configured via GitHub Actions (`.github/workflows/backend-ci.yml`):  
build → test → upload artifact → deploy via Docker Compose on self-hosted runner.

---

## 🇺🇦 Українська

### Огляд

FitHub — це повностекова платформа для управління фітнес-студією, розроблена для тренажерних залів, персональних тренерів та їхніх клієнтів. Платформа надає централізований командний центр для управління тренуваннями, планами харчування, абонементами, живими відвідуваннями та аналітикою продуктивності.

### ✨ Функціональність

| Модуль | Опис |
|---|---|
| **Автентифікація** | JWT (RSA-підпис), верифікація email, скидання пароля, чорний список токенів, обмеження запитів |
| **Профілі користувачів** | Окремі профілі тренера та клієнта з повним CRUD |
| **Плани тренувань** | Створення, призначення та відстеження структурованих програм тренувань із денним розкладом вправ |
| **Журнал тренувань** | Запис окремих вправ із підходами, повтореннями, вагою, тривалістю та складністю |
| **Тренувальні сесії** | Розклад групових/персональних занять, запис клієнтів, реєстрація відвідування з валідацією абонементу |
| **Абонементи** | Місячні, річні або відвідувальні абонементи із заморозкою/розморозкою/продовженням/скасуванням |
| **Платежі** | Обробка платежів з опціональною валідацією криптовалюти TRON |
| **Харчування** | Плани харчування, база продуктів, відстеження споживання води з добовою/тижневою аналітикою |
| **Відстеження прогресу** | Антропометричні вимірювання, фітнес-цілі, особисті рекорди, фото прогресу |
| **Сповіщення** | Сповіщення в додатку, email (асинхронно), інтеграція з Telegram-ботом |
| **Відгуки** | Відгуки клієнтів про тренерів з модерацією та агрегацією рейтингу |
| **Аналітика дашборду** | Активні клієнти, виручка, добові відвідування, популярні сесії, графіки відвідуваності |
| **Файлове сховище** | Завантаження відео та зображень через MinIO з підтримкою range-запитів |
| **Кешування** | Багаторівневе кешування Redis з гранульованим TTL для кожного типу сутності |
| **Моніторинг** | Метрики Prometheus + дашборди Grafana, моніторинг пулу з'єднань HikariCP |

### 🏗️ Архітектура

```
fithub/
├── backend/                      # Spring Boot додаток
│   └── src/main/java/
│       └── com/dev/quikkkk/
│           ├── core/             # Спільне: конфігурація, безпека, винятки, DTO
│           └── modules/          # Функціональні модулі
└── frontend/                     # React + TypeScript SPA
    └── src/
        ├── pages/                # Компоненти рівня маршруту
        ├── components/           # Перевикористовувані UI-компоненти
        ├── services/             # Axios API шар
        ├── store/                # Управління станом Zustand
        └── layouts/              # Макети оболонки додатку
```

### 🛠️ Технологічний стек

**Backend**
- Java 21 · Spring Boot 4.0.3 · Spring Security · Spring Data JPA
- PostgreSQL · Redis · MinIO
- JWT (асиметричні ключі RSA) · Flyway міграції
- Prometheus + Grafana · Docker Compose

**Frontend**
- React 19 · TypeScript 5.9 · Vite 7
- Tailwind CSS 3 · Framer Motion
- Zustand · React Router 7 · Recharts · Axios

### 🚀 Швидкий старт

**Вимоги:** Java 21, Node.js 20+, Docker

```bash
# 1. Клонувати репозиторій
git clone https://github.com/your-username/fithub.git
cd fithub

# 2. Запустити інфраструктуру (PostgreSQL, Redis, MinIO, MailDev)
cd docker
docker compose -f docker-compose-dev.yml up -d

# 3. Налаштувати середовище
cp env.example env.properties
# Відредагувати env.properties

# 4. Запустити backend
./mvnw spring-boot:run

# 5. Запустити frontend
cd frontend
npm install
npm run dev
```

Backend запускається на `http://localhost:8080`, frontend на `http://localhost:5173`.  
Документація API: `http://localhost:8080/swagger-ui.html`

### ⚙️ Змінні середовища

| Змінна | Опис |
|---|---|
| `DB_URL` | JDBC URL PostgreSQL |
| `DB_USERNAME` | Ім'я користувача БД |
| `DB_PASSWORD` | Пароль БД |
| `REDIS_HOST` | Хост Redis |
| `REDIS_PASSWORD` | Пароль Redis |
| `JWT_ACCESS_TOKEN_EXPIRATION` | TTL токена доступу в мс |
| `JWT_REFRESH_TOKEN_EXPIRATION` | TTL токена оновлення в мс |
| `FRONTEND_URL` | Origin фронтенду для CORS та посилань в email |
| `TELEGRAM_TOKEN` | Токен Telegram-бота (опціонально) |

### 🔐 Безпека

- Асиметрична пара ключів RSA для підпису/верифікації JWT
- Чорний список токенів через Redis (інвалідація при logout)
- Обмеження входу за IP-адресою
- Блокування одноразових email-адрес
- `@PreAuthorize` рольовий доступ на кожному ендпоінті
- Розподілене блокування сесій (Redis) для безпечної паралельної реєстрації відвідувань

### 📦 Розгортання

```bash
cd docker
docker compose build --no-cache backend
docker compose up -d
```

CI/CD налаштовано через GitHub Actions (`.github/workflows/backend-ci.yml`):  
збірка → тести → завантаження артефакту → розгортання через Docker Compose на self-hosted runner.

---

## 🇷🇺 Русский

### Обзор

FitHub — это full-stack платформа для управления фитнес-студией, разработанная для тренажёрных залов, персональных тренеров и их клиентов. Платформа предоставляет централизованный командный центр для управления тренировками, планами питания, абонементами, живыми посещениями и аналитикой производительности.

### ✨ Функциональность

| Модуль | Описание |
|---|---|
| **Аутентификация** | JWT (RSA-подпись), верификация email, сброс пароля, чёрный список токенов, ограничение запросов |
| **Профили пользователей** | Отдельные профили тренера и клиента с полным CRUD |
| **Планы тренировок** | Создание, назначение и отслеживание структурированных программ тренировок с дневным расписанием упражнений |
| **Журнал тренировок** | Запись отдельных упражнений с подходами, повторениями, весом, длительностью и сложностью |
| **Тренировочные сессии** | Расписание групповых/персональных занятий, запись клиентов, регистрация посещения с валидацией абонемента |
| **Абонементы** | Месячные, годовые или посещаемые абонементы с заморозкой/разморозкой/продлением/отменой |
| **Платежи** | Обработка платежей с опциональной валидацией криптовалюты TRON |
| **Питание** | Планы питания, база продуктов, отслеживание потребления воды с суточной/недельной аналитикой |
| **Отслеживание прогресса** | Антропометрические измерения, фитнес-цели, личные рекорды, фото прогресса |
| **Уведомления** | Уведомления в приложении, email (асинхронно), интеграция с Telegram-ботом |
| **Отзывы** | Отзывы клиентов о тренерах с модерацией и агрегацией рейтинга |
| **Аналитика дашборда** | Активные клиенты, выручка, суточные посещения, популярные сессии, графики посещаемости |
| **Файловое хранилище** | Загрузка видео и изображений через MinIO с поддержкой range-запросов |
| **Кэширование** | Многоуровневое кэширование Redis с гранулярным TTL для каждого типа сущности |
| **Мониторинг** | Метрики Prometheus + дашборды Grafana, мониторинг пула соединений HikariCP |

### 🏗️ Архитектура

```
fithub/
├── backend/                      # Spring Boot приложение
│   └── src/main/java/
│       └── com/dev/quikkkk/
│           ├── core/             # Общее: конфигурация, безопасность, исключения, DTO
│           └── modules/          # Функциональные модули
└── frontend/                     # React + TypeScript SPA
    └── src/
        ├── pages/                # Компоненты уровня маршрута
        ├── components/           # Переиспользуемые UI-компоненты
        ├── services/             # Axios API слой
        ├── store/                # Управление состоянием Zustand
        └── layouts/              # Макеты оболочки приложения
```

### 🛠️ Технологический стек

**Backend**
- Java 21 · Spring Boot 4.0.3 · Spring Security · Spring Data JPA
- PostgreSQL · Redis · MinIO
- JWT (асимметричные ключи RSA) · Flyway миграции
- Prometheus + Grafana · Docker Compose

**Frontend**
- React 19 · TypeScript 5.9 · Vite 7
- Tailwind CSS 3 · Framer Motion
- Zustand · React Router 7 · Recharts · Axios

### 🚀 Быстрый старт

**Требования:** Java 21, Node.js 20+, Docker

```bash
# 1. Клонировать репозиторий
git clone https://github.com/your-username/fithub.git
cd fithub

# 2. Запустить инфраструктуру (PostgreSQL, Redis, MinIO, MailDev)
cd docker
docker compose -f docker-compose-dev.yml up -d

# 3. Настроить окружение
cp env.example env.properties
# Отредактировать env.properties

# 4. Запустить backend
./mvnw spring-boot:run

# 5. Запустить frontend
cd frontend
npm install
npm run dev
```

Backend запускается на `http://localhost:8080`, frontend на `http://localhost:5173`.  
Документация API: `http://localhost:8080/swagger-ui.html`

### ⚙️ Переменные окружения

| Переменная | Описание |
|---|---|
| `DB_URL` | JDBC URL PostgreSQL |
| `DB_USERNAME` | Имя пользователя БД |
| `DB_PASSWORD` | Пароль БД |
| `REDIS_HOST` | Хост Redis |
| `REDIS_PASSWORD` | Пароль Redis |
| `JWT_ACCESS_TOKEN_EXPIRATION` | TTL токена доступа в мс |
| `JWT_REFRESH_TOKEN_EXPIRATION` | TTL токена обновления в мс |
| `FRONTEND_URL` | Origin фронтенда для CORS и ссылок в email |
| `TELEGRAM_TOKEN` | Токен Telegram-бота (опционально) |

### 🔐 Безопасность

- Асимметричная пара ключей RSA для подписи/верификации JWT
- Чёрный список токенов через Redis (инвалидация при logout)
- Ограничение входа по IP-адресу
- Блокировка одноразовых email-адресов
- `@PreAuthorize` ролевой доступ на каждом эндпоинте
- Распределённая блокировка сессий (Redis) для безопасной параллельной регистрации посещений

### 📦 Развёртывание

```bash
cd docker
docker compose build --no-cache backend
docker compose up -d
```

CI/CD настроен через GitHub Actions (`.github/workflows/backend-ci.yml`):  
сборка → тесты → загрузка артефакта → развёртывание через Docker Compose на self-hosted runner.

---

<div style="text-align: center;">

Made with ❤️ by the QuiK000

</div>