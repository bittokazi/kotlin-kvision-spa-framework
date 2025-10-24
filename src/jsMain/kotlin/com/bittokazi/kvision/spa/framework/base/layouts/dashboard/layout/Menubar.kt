package com.bittokazi.kvision.spa.framework.base.layouts.dashboard.layout

import com.bittokazi.kvision.spa.framework.base.common.SpaAppEngine
import com.bittokazi.kvision.spa.framework.base.common.ObservableManager
import com.bittokazi.kvision.spa.framework.base.common.SpaApplication
import io.kvision.core.Container
import io.kvision.html.*
import kotlinx.browser.window
import kotlin.collections.forEach
import kotlin.collections.get

fun Container.dashboardMenuBar() {

    val menuSections = SpaAppEngine.spaAuthService.spaUser?.spaRoles?.get(0)?.let {
        SpaApplication.applicationConfiguration.menuProvider.getMenu(it)
    }?:run { listOf() }
    val items: MutableMap<String, Li> = mutableMapOf()
    val parents: MutableMap<String, Li> = mutableMapOf()

    ul(className = "sidebar-nav") {
        menuSections.listIterator().forEach { menuSection ->
            li(className = "sidebar-header", content = menuSection.title)

            menuSection.menuItems.listIterator().forEach {
                when (it.subMenuItems.isEmpty()) {
                    true -> {
                        items[it.link?: run { "" }] = Li(className = "sidebar-item") {
                            link("", it.link, className = "sidebar-link", dataNavigo = !it.external) {
                                if(it.external) {
                                    target = "_blank"
                                }
                                add(i(className = "align-middle") {
                                    setAttribute("data-feather", it.icon)
                                })
                                add(span(className = "align-middle", content = it.title))
                            }
                        }
                        add(items[it.link]!!)
                    }
                    else -> {
                        val parent = Li(className = "sidebar-item") {
                            link("", null, className = "sidebar-link collapsed") {
                                add(i(className = "align-middle") {
                                    setAttribute("data-feather", it.icon)
                                })
                                add(span(className = "align-middle", content = it.title))
                                setAttribute("data-bs-target", "#${it.title}")
                                setAttribute("data-bs-toggle", "collapse")
                            }
                            ul(className = "sidebar-dropdown list-unstyled collapse") {
                                id = it.title
                                setAttribute("data-bs-parent", "#sidebar")

                                it.subMenuItems.listIterator().forEach { sub ->
                                    items[sub.link?: run { "" }] = Li(className = "sidebar-item") {
                                        link("", sub.link, className = "sidebar-link", dataNavigo = true) {
                                            if(it.external) {
                                                target = "_blank"
                                            }
                                            add(span(className = "align-middle", content = sub.title))
                                        }
                                    }
                                    add(items[sub.link]!!)
                                }
                            }
                        }

                        it.subMenuItems.listIterator().forEach { sub ->
                            parents[sub.link?: run { "" }] = parent
                        }
                        add(parent)
                    }
                }
            }
        }
    }

    ObservableManager.setSubscriber("menuBar") {
        SpaAppEngine.dashboardPageChangeObserver.subscribe {
            window.setTimeout({
                items.keys.forEach {
                    if(items[it]?.hasCssClass("active") == true) {
                        items[it]?.removeCssClass("active")
                    }
                    if(it == window.location.pathname) {
                        items[it]?.addCssClass("active")
                    }
                }
                parents.keys.forEach {
                    if(parents[it]?.hasCssClass("active") == true) {
                        parents[it]?.removeCssClass("active")
                    }
                }
                parents.keys.forEach {
                    if(it == window.location.pathname) {
                        parents[it]?.getChildren()?.get(0)?.removeCssClass("collapsed")
                        parents[it]?.getChildren()?.get(0)?.setAttribute("aria-expanded", "true")
                        if (parents[it]?.getChildren()?.get(1)?.hasCssClass("show") == false) {
                            parents[it]?.getChildren()?.get(1)?.addCssClass("show")
                        }
                        parents[it]?.addCssClass("active")
                    }
                }
            }, 50)
        }
    }
}
