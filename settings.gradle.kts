pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "Ecommerce"
include(":app")
include(":core")
include(":account")
include(":shared-ui")
include(":auth")
include(":cart")
include(":checkout")
include(":transaction")
include(":wishlist")
include(":search")
include(":voucher")
include(":settings")
include(":shared-auth")
include(":shared-events")
include(":product")
include(":shared-product")
include(":shared-cart")
include(":shared-wishlist")
include(":shared-voucher")
include(":shared-user")
include(":shared-transaction")
include(":background-work")
