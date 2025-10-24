package com.bittokazi.kvision.spa.framework.base.components.menu

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
    var external: Boolean = false
)

interface MenuProvider {
    fun getMenu(spaRole: SpaRole): List<MenuSection>
}
