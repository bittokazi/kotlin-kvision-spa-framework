# Kotlin KVision SPA Framework

A **Single-Page Application (SPA) framework** built with [KVision](https://kvision.io) and Kotlin/JS.  
This library provides a clean, modular foundation for developing reactive SPAs in Kotlin — featuring authentication, routing, multi-tenancy, and lifecycle management.

---

## 🚀 Overview

`kotlin-kvision-spa-framework` simplifies SPA development in Kotlin by providing:
- A ready-to-use application lifecycle (`SpaApplication`)
- Built-in authentication and refresh token handling
- Tenant-aware configuration (`SpaTenantInfo`)
- Modular routing and root module setup
- Integrated REST service and reactive state management
- A built-in dashboard template via `DefaultSecuredModule`
- Support for modular routing via `DefaultRootApplicationModule`
- Per-feature secured modules via `DefaultSecuredPageModule`

It consists of:
- A **Kotlin/JS dependency**
- A **companion NPM package** for static resources (CSS, JS, icons, etc.)

---

## 📦 Installation

Add the following to your **Kotlin Multiplatform Gradle** project:

```kotlin
plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion
}

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()

kotlin {
    js(IR) {
        browser {
            useEsModules()
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
                sourceMaps = false
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
        compilerOptions {
            target.set("es2015")
        }
    }
    sourceSets["jsMain"].dependencies {
        implementation("io.kvision:kvision:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
        implementation("io.kvision:kvision-routing-navigo-ng:$kvisionVersion")
        implementation("io.kvision:kvision-state-flow:$kvisionVersion")

        // ✅ Add the SPA framework dependencies
        implementation("com.bittokazi.sonartype:kotlinKvisionSpaFramework-js:1.0.7")
        implementation(npm("kotlin-kvision-spa-framework-resources", "1.0.7"))
    }
}
```

---

## 🧠 Quick Start Example

Example `main()` setup using the framework:

```kotlin
SpaApplication.init()

SpaAppEngine.restService.REFRESH_TOKEN_ENDPOINT =
    "${SpaAppEngine.restService.BASE_URL}/login/refresh/token"

AppEngine.restService = SpaAppEngine.restService
AppEngine.authService = AuthService()
AppEngine.tenantService = TenantService()
AppEngine.userService = UserService()

SpaApplication.applicationConfiguration = ApplicationConfiguration(
    spaTenantInfo = SpaTenantInfo(
        cpanel = false,
        enabledConfigPanel = false,
        name = "SpaApplication"
    ),
    isTenantEnabled = true,
    rootApplicationModule = rootModule(),
    tenantInformationProvider = AppEngine.tenantService,
    authHolderType = AuthHolderType.LOCAL_STORAGE,
    menuProvider = AppEngine.authService,
    refreshTokenRequestProvider = AppEngine.authService,
    logoutActionProvider = AppEngine.authService
)

SpaApplication.start()
```

---

## 🧩 Using `DefaultRootApplicationModule`

Defines the public routes (login, signup, home) and links to the secured dashboard module.

```kotlin
fun rootModule(): ApplicationModule = DefaultRootApplicationModule(
    loginPage = { LoginPage() },
    securedModule = dashboardModule(),
    authInformationProvider = AppEngine.authService,
    RouterConfiguration(
        route = "/app",
        title = "App Home",
        view = { HomePage() }
    ),
    RouterConfiguration(
        route = "/app/signup",
        title = "Sign Up",
        view = { SignupPage() }
    )
)
```

| Parameter | Description |
|------------|-------------|
| **`loginPage`** | Defines the login component |
| **`securedModule`** | The authenticated dashboard area |
| **`RouterConfiguration`** | Adds public routes like `/app` or `/app/signup` |

---

## 🔐 Using `DefaultSecuredModule`

Provides a complete dashboard shell for authenticated areas.

```kotlin
fun dashboardModule() = DefaultSecuredModule(
    layoutLoader = DefaultLayoutLoader(),
    modules = listOf(
        userModule(),
        tenantModule(),
        pageModule(),
        categoryModule(),
        postModule(),
        commentModule()
    ),
    RouterConfiguration(
        route = AppEngine.APP_DASHBOARD_ROUTE,
        title = "Dashboard Home",
        view = {
            Div {
                p { content = "Hello Dashboard!!!" }
            }
        },
        dashboardContainer = ContentContainerType.CARD
    )
)
```

| Parameter | Description |
|------------|-------------|
| **`layoutLoader`** | Defines the dashboard layout |
| **`modules`** | Registers feature modules (users, posts, etc.) |
| **`RouterConfiguration`** | Defines a home or landing route |
| **`ContentContainerType`** | Controls layout container style |

---

## 📘 Using `DefaultSecuredPageModule`

The `DefaultSecuredPageModule` represents a single feature or section within the dashboard —  
for example, the *Users* management section.

```kotlin
package cms.tenant.multi.frontend.project.app.private.dashboard.user

import cms.tenant.multi.frontend.project.app.private.dashboard.user.pages.AddUserPage
import cms.tenant.multi.frontend.project.app.private.dashboard.user.pages.UsersPage
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import com.bittokazi.kvision.spa.framework.base.common.module.DefaultSecuredPageModule

fun userModule() = DefaultSecuredPageModule(
    RouterConfiguration(
        route = "/app/dashboard/users",
        title = "All Users",
        view = { UsersPage() }
    ),
    RouterConfiguration(
        route = "/app/dashboard/users/add",
        title = "Add User",
        view = { AddUserPage() }
    )
)
```

| Parameter | Description |
|------------|-------------|
| **`RouterConfiguration`** | Defines one or more routes for this section |
| **`view`** | Specifies which component (page) to render |
| **`title`** | Used for navigation and breadcrumbs |
| **`route`** | Defines the relative URL path within the dashboard |

This allows you to modularize each section of your app — users, posts, settings, etc.

---

## 🧱 Example View Container

Here’s an example of a simple **page container view** (`UsersPage`) used inside a module:

```kotlin
package cms.tenant.multi.frontend.project.app.private.dashboard.user.pages

import cms.tenant.multi.frontend.project.base.common.AppEngine
import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import io.kvision.html.*
import io.kvision.panel.SimplePanel

class UsersPage : SimplePanel() {

    init {
        table(className = "table table-hover my-0") {
            thead {
                tr {
                    th { content = "#" }
                    th { content = "Email" }
                    th { content = "Name" }
                    th { content = "Role" }
                    th { content = "Actions" }
                }
            }
            tbody {
                AppEngine.userService.getAll().then {
                    it.data.forEachIndexed { index, user ->
                        tr {
                            td { content = "${index + 1}" }
                            td { content = user.email }
                            td { content = "${user.firstName} ${user.lastName}" }
                            td { content = user.roles?.get(0)?.name }
                            td { /* Action buttons go here */ }
                        }
                    }
                }.then {
                    SpaAppEngine.routing.updatePageLinks()
                }
            }
        }
    }
}
```

This demonstrates:
- Fetching data from a service (`AppEngine.userService`)
- Rendering it dynamically in a table
- Updating links using `SpaAppEngine.routing.updatePageLinks()`

---

## ⚙️ Commands

### ▶️ Run with Hot Reload
```bash
./gradlew run
```

### 🏗️ Build for Production
```bash
./gradlew build
```

---

## 📜 License

Licensed under the **MIT License**.  
See [LICENSE](LICENSE) for details.

---

## 💬 Support & Contributing

- Open issues or feature requests on GitHub
- Fork the repo and submit pull requests
- Use GitHub Discussions for community Q&A

---