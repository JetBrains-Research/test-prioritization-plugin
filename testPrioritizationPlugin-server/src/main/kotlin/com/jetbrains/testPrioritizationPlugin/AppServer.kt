package com.jetbrains.testPrioritizationPlugin

import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppServer(private val descriptor: PluginDescriptor, manager: WebControllerManager) : BaseController() {
    init {
        manager.registerController("/hello.html", this)
    }

    override fun doHandle(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
    ): ModelAndView {

        return ModelAndView(descriptor.getPluginResourcesPath("Hello.jsp"))
    }
}
