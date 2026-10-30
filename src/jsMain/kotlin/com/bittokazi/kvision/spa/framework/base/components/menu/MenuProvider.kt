package com.bittokazi.kvision.spa.framework.base.components.menu

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.models.SpaRole

data class MenuSection (
    var title: String?,
    var link: String?,
    var icon: String,
    var menuItems: List<MenuItem> = listOf(),
    var external: Boolean = false
)

data class MenuItem (
    var title: String?,
    var link: String? = "",
    var icon: String = "",
    var subMenuItems: List<MenuItem> = listOf(),
    var external: Boolean = false,
    var accessCheckOnly: Boolean = false
)

interface MenuProvider {
    fun getMenu(spaRole: SpaRole): List<MenuSection>

    fun hasAccessToPage(spaRoles: List<SpaRole>, route: String): Boolean {
        return !SpaAppEngine.PAGE_ACCESS_CHECK || (SpaAppEngine.APP_DASHBOARD_ACCOUNT_SETTINGS_ROUTE == route
            || SpaAppEngine.APP_DASHBOARD_ACCOUNT_SECURITY_ROUTE == route) || spaRoles.any { role ->
            getMenu(role).any { section ->
                section.link.matchesRoute(route) || section.menuItems.any { it.containsRoute(route) }
            }
        }
    }
}

private fun MenuItem.containsRoute(route: String): Boolean =
    link.matchesRoute(route) || subMenuItems.any { it.containsRoute(route) }

private fun String?.matchesRoute(route: String): Boolean {
    val pattern = this ?: return false
    val patternSegments = pattern.split("/")
    val routeSegments = route.split("/")
    return patternSegments.size == routeSegments.size &&
        patternSegments.indices.all { index ->
            patternSegments[index].startsWith(":") || patternSegments[index] == routeSegments[index]
        }
}
