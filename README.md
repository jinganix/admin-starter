[![CI](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[中文版本](README.zh.md)

# admin-starter

## Demo

Please select a faster frontend page address to view the demo:

- Netlify: https://admin-starter.netlify.app/
- Self-hosted: https://admin-starter.u3d.cc/

## User Experience Journey

You can follow the steps below to experience the basic functions.

Note that during your operation, other accounts may be operating concurrently.

Ensure that your operation steps are not affected by other accounts.

If you encounter any issues, please submit an [issue](https://github.com/jinganix/admin-starter/issues).

- Register an account, assuming the username is `username`, and register successfully to log in to the system
  - New registered accounts have the administrator role by default.

- Click on the left sidebar `System` - `Permissions`, enter the permission management page, and click on the `Sync UI` and `Reload API` buttons in the top right corner of the table
  - Clicking `Sync UI` will synchronize the frontend permissions to the database
  - Clicking `Reload API` will load all backend API permissions into the database

- Click on the left sidebar `System` - `Roles`, enter the role management page.
- Click the `Add` button, add a role, assuming the role name is `role`, and select all permissions except:
  - `Button` - `Add User`
  - `Menu` - `System` - `Audits`
  - `System` - `User` - `Update user status`

- Click on the left sidebar `System` - `Users`, enter the user management page, and search for your own user by `username`
- Click on the right-hand side `···` - `Edit`, in the edit box, select only the newly created role `role`, and save the changes
  - At this point, your user `username` has the `role` role and all the permissions of `role`, except for the three permissions that were not selected

- Click on the top right corner of the page, select `Refresh` to refresh your user data and permission data. After the data is refreshed, the following changes will occur:
  - The `+ Add` button is no longer displayed, because `role` does not have the `Button` - `Add User` permission
  - The left sidebar no longer displays the `System` - `Audit` menu, because `role` does not have the `Menu` - `System` - `Audit` permission
  - When you click on the status switch button for users other than `admin`, you will be redirected to an error page, because `role` does not have the `System` - `User` - `Update User Status` API permission

- Finally, you can assign all the permissions to the `role` role in the `System` - `Roles` page. After refreshing the user data, you can view the effect.

## Running the Project

To run the project, you need to first clone the source code and navigate to the project's root directory.

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter
```

### Backend

#### Running with docker-compose

If you have `docker` and `docker-compose` installed, you can start the backend service using the following command:

```shell
./gradlew build
docker-compose up --build
```

#### Running with Gradle

You need to install JDK with the corresponding version specified in [.tool-versions](.tool-versions). Start a `MySQL` database and `Redis`.

Modify the application-local.yml file to configure the backend services of the project.

The following command can be used to start the backend services on a Linux or macOS system:

```shell
./gradlew service:guess:bootRun
```

The following command can be used to start the backend services on a Windows system:

```shell
./gradlew.bat service:admin-starter:bootRun
```

### Frontend

You need to install [node.js](https://nodejs.org/en) with the version specified in [.tool-versions](.tool-versions).

#### Running the Commands

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter/frontend/admin
npm install
npm start
```

## Contributing

If you are interested in reporting/fixing issues and contributing directly to the code base, please see [CONTRIBUTING.md](CONTRIBUTING.md) for more information on what we're looking for and how to get started.
