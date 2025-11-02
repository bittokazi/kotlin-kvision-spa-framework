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
package cms.tenant.multi.frontend.project

import cms.tenant.multi.frontend.project.app.private.dashboard.tenant.TenantService
import cms.tenant.multi.frontend.project.app.private.dashboard.user.UserService
import cms.tenant.multi.frontend.project.app.rootModule
import cms.tenant.multi.frontend.project.base.common.AppEngine
import cms.tenant.multi.frontend.project.base.services.AuthService
import com.bittokazi.kvision.spa.framework.base.common.ApplicationConfiguration
import com.bittokazi.kvision.spa.framework.base.common.AuthHolderType
import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import com.bittokazi.kvision.spa.framework.base.models.SpaTenantInfo
import com.bittokazi.kvision.spa.framework.base.utils.importDefaultResources

fun main() {

    importDefaultResources()
    SpaApplication.init()

    // Configure API endpoints
    SpaAppEngine.restService.REFRESH_TOKEN_ENDPOINT =
        "${SpaAppEngine.restService.BASE_URL}/login/refresh/token"

    // Setup your application services
    AppEngine.restService = SpaAppEngine.restService
    AppEngine.authService = AuthService()
    AppEngine.tenantService = TenantService()
    AppEngine.userService = UserService()

    // Configure application behavior
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

    // Start the SPA
    SpaApplication.start()
}
```

---

## 🧩 Using `DefaultRootApplicationModule`

The `DefaultRootApplicationModule` is a prebuilt module that defines the public routes (login, signup, etc.) and the entry point to secured modules (like dashboards).

Example usage:

```kotlin
package cms.tenant.multi.frontend.project.app

import cms.tenant.multi.frontend.project.app.private.dashboardModule
import cms.tenant.multi.frontend.project.app.public.HomePage
import cms.tenant.multi.frontend.project.app.public.signin.LoginPage
import cms.tenant.multi.frontend.project.app.public.signin.OauthLoginPage
import cms.tenant.multi.frontend.project.app.public.signup.SignupPage
import cms.tenant.multi.frontend.project.base.common.AppEngine
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import com.bittokazi.kvision.spa.framework.base.common.module.ApplicationModule
import com.bittokazi.kvision.spa.framework.base.common.module.DefaultRootApplicationModule

fun rootModule(): ApplicationModule = DefaultRootApplicationModule(
    loginPage = {
        LoginPage()
    },
    securedModule = dashboardModule(),
    authInformationProvider = AppEngine.authService,
    RouterConfiguration(
        route = "/app",
        title = "App Home",
        view = {
            HomePage()
        }
    ),
    RouterConfiguration(
        route = "/app/signup",
        title = "Sign Up",
        view = {
            SignupPage()
        }
    )
)
```

### 🔍 Explanation

| Parameter | Description |
|------------|-------------|
| **`loginPage`** | Defines the public login page component |
| **`securedModule`** | Links to a secured area (see `DefaultSecuredModule` below) |
| **`authInformationProvider`** | Provides authentication details and validation |
| **`RouterConfiguration`** | Adds public routes to your SPA (e.g., `/app`, `/app/signup`) |

This setup defines both your public routes and how users transition into the secured part of your application after authentication.

---

## 🔐 Using `DefaultSecuredModule`

The `DefaultSecuredModule` provides a ready-made structure for your authenticated (dashboard) area.  
It supports modular sections, routing, and a customizable layout via `DefaultLayoutLoader`.

Example:

```kotlin
package cms.tenant.multi.frontend.project.app.private

import cms.tenant.multi.frontend.project.app.private.dashboard.category.categoryModule
import cms.tenant.multi.frontend.project.app.private.dashboard.comment.commentModule
import cms.tenant.multi.frontend.project.app.private.dashboard.page.pageModule
import cms.tenant.multi.frontend.project.app.private.dashboard.post.postModule
import cms.tenant.multi.frontend.project.app.private.dashboard.tenant.tenantModule
import cms.tenant.multi.frontend.project.app.private.dashboard.user.userModule
import cms.tenant.multi.frontend.project.base.common.AppEngine
import com.bittokazi.kvision.spa.framework.base.common.RouterConfiguration
import com.bittokazi.kvision.spa.framework.base.common.module.DefaultSecuredModule
import com.bittokazi.kvision.spa.framework.base.layouts.DefaultLayoutLoader
import com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout.ContentContainerType
import io.kvision.html.*

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
                p {
                    content = "Hello Dashboard!!!"
                }
            }
        },
        dashboardContainer = ContentContainerType.CARD
    )
)
```

### 🔍 Explanation

| Parameter | Description |
|------------|-------------|
| **`layoutLoader`** | Provides the main dashboard layout (sidebars, navbar, etc.) |
| **`modules`** | Registers dashboard feature modules (e.g., posts, users, tenants) |
| **`RouterConfiguration`** | Defines a default route or dashboard landing view |
| **`ContentContainerType`** | Determines how content is displayed (`CARD`, `FULL_WIDTH`, etc.) |

This allows you to build complex dashboards quickly by composing smaller modules.

---

## ⚙️ Commands

### ▶️ Run with Hot Reload

Launch the app in development mode:

```bash
./gradlew run
```

### 🏗️ Build for Production

Generate a production-ready build:

```bash
./gradlew build
```

---

## 📜 License

Licensed under the **MIT License**.  
See [LICENSE](LICENSE) for details.

---

## 💬 Support & Contributing

- Open an issue or feature request on GitHub
- Fork the repo and submit a pull request
- Use GitHub Discussions for community Q&A

---