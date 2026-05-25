[![CI](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml)
[![Backend coverage](https://codecov.io/gh/jinganix/admin-starter/flags/backend/graph/badge.svg?branch=master)](https://codecov.io/gh/jinganix/admin-starter/flags/backend)
[![Frontend coverage](https://codecov.io/gh/jinganix/admin-starter/flags/frontend/graph/badge.svg?branch=master)](https://codecov.io/gh/jinganix/admin-starter/flags/frontend)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[中文版本](README.zh.md)

# admin-starter

A full-stack admin dashboard starter with role-based access control (RBAC). Use the hosted demo to explore permission sync, role assignment, and dynamic UI visibility, or run the Spring Boot backend and React frontend locally.

## Contents

- [Demo](#demo)
- [Guided tour](#guided-tour)
- [Configuration](#configuration)
- [Getting started](#getting-started)
- [Contributing](#contributing)

## Demo

Pick a frontend URL with better latency for your region:

| Region | URL |
|--------|-----|
| Global (Netlify) | https://admin-starter.netlify.app/ |
| China | https://admin-starter.u3d.cc/ |

## Guided tour

Walk through these steps to try core RBAC features. Other accounts may be using the demo at the same time—keep your steps isolated from theirs. If anything looks wrong, [open an issue](https://github.com/jinganix/admin-starter/issues).

### 1. Register and sign in

- Register an account (e.g. username `username`). You are logged in automatically after registration.
- By default, new accounts receive the **administrator** role and can perform all operations (see [Configuration](#configuration) to change this).
- Alternatively, sign in with the bootstrap account: username `admin`, password `aaaaaa`.

### 2. Sync permissions

- Open **System → Permissions** in the left sidebar.
- Click **Sync UI** to upload frontend permission definitions to the database.
- Click **Reload API** to load backend API permissions from code into the database.

### 3. Create a restricted role

- Open **System → Roles** and click **Add**.
- Create a role named `role` and select all permissions **except**:
  - **Button → Add User**
  - **Menu → System → Audits**
  - **System → User → Update user status**

### 4. Assign the restricted role to your user

- Open **System → Users**, search for `username`.
- Click **··· → Edit**, select only the new role `role`, and save.
- Your user now has every permission in `role`, minus the three excluded above.

### 5. Verify restrictions

- Sign in as `username` (or click your avatar → **Refresh** if already logged in).
- Expected changes:
  - The **+ Add** button disappears (`role` lacks **Button → Add User**).
  - **System → Audit** no longer appears in the sidebar (`role` lacks **Menu → System → Audit**).
  - Toggling status for users other than `admin` redirects to an error page (`role` lacks **System → User → Update User Status**).

### 6. Restore full access

- On **System → Roles**, grant all permissions to `role`, then refresh your user data again to see the UI return to normal.

## Configuration

Signup role assignment is controlled in [service/src/main/resources/application.yml](service/src/main/resources/application.yml):

```yaml
config:
  signup:
    register-as-admin: true
```

| Value | Behavior |
|-------|----------|
| `true` | **Default.** New registrations receive the administrator role and can access all features. Suitable for demos and local development. |
| `false` | New registrations receive no database role. Users can only access self-service endpoints (profile, password) until an administrator assigns a business role. Suitable for production. |

Override at runtime, for example:

```shell
./gradlew service:bootRun --args='--config.signup.register-as-admin=false'
```

## Getting started

### Prerequisites

| Tool | Notes |
|------|-------|
| JDK | Version in [.tool-versions](.tool-versions) |
| Node.js | Version in [.tool-versions](.tool-versions) |
| MySQL & Redis | Required when running the backend with Gradle (provided by Docker Compose) |
| Docker & Docker Compose | Optional; easiest way to run the backend |

### Clone the repository

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter
```

### Backend

#### Option A: Docker Compose (recommended)

```shell
./gradlew build
docker-compose up --build
```

The API is available at `http://localhost:8080`.

#### Option B: Gradle

1. Install JDK per [.tool-versions](.tool-versions).
2. Start MySQL and Redis locally.
3. Adjust [service/src/main/resources/application-local.yml](service/src/main/resources/application-local.yml) if your connection settings differ from the defaults.

**Linux / macOS:**

```shell
./gradlew service:bootRun
```

**Windows:**

```shell
./gradlew.bat service:bootRun
```

### Frontend

From the project root:

```shell
cd frontend
npm install
npm start
```

## Contributing

Interested in reporting or fixing issues, or contributing code? See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines and how to get started.
