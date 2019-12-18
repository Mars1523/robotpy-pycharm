package toolwindow

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import runconfig.RobotpyRunConfiguration
import runconfig.RobotpyRunConfigurationType
import runconfig.RobotpyRunnerConfigurationFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JComponent

class RobotpyToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolwindowPanel = RobotpyToolWindowPanel(project)
        val tab = ContentFactory.SERVICE.getInstance().createContent(toolwindowPanel, "", false)
        toolWindow.contentManager.addContent(tab)
    }
}

class RobotpyToolWindowPanel(project: Project) : SimpleToolWindowPanel(true, false) {
    private val LOG = Logger.getInstance(this::class.java)

    private val toolbar: ActionToolbar = run {
        val actionManager = ActionManager.getInstance()
        actionManager.createActionToolbar(
            "Robotpy Toolbar",
            actionManager.getAction("Robotpy.Toolbar") as DefaultActionGroup,
            true
        )
    }

    private val tree = JBList(lessCommands)

    private val scroll: JComponent = ScrollPaneFactory.createScrollPane(tree, 0)

    fun showFullCommands() {
        tree.setListData(fullCommands)
        LOG.trace("Showing all commands")
    }

    fun showLessCommands() {
        tree.setListData(lessCommands)
        LOG.trace("Showing less commands")
    }

    init {
        setContent(this.scroll)
        setToolbar(this.toolbar.component)

        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount < 2) return
                val list = e.source as? JBList<*> ?: return
                val index = tree.locationToIndex(e.point)
                val command = list.model.getElementAt(index) as String
                LOG.info("User clicked command: $command, index $index")

                val runManager = RunManager.getInstance(project)
                val configuration =
                    runManager
                        .createConfiguration(
                            "RobotPy: $command",
                            RobotpyRunnerConfigurationFactory(RobotpyRunConfigurationType.getInstance())
                        )
                val robotpyConfig = configuration.configuration as RobotpyRunConfiguration

                when {
                    command.startsWith("coverage ") -> {
                        robotpyConfig.coverage = true
                        robotpyConfig.command = command.slice("coverage ".length until command.length)
                    }
                    command.startsWith("profiler ") -> {
                        robotpyConfig.profile = true
                        robotpyConfig.command = command.slice("profiler ".length until command.length)
                    }
                    else -> {
                        robotpyConfig.command = command
                    }
                }

                runManager.setTemporaryConfiguration(configuration)

                ProgramRunnerUtil.executeConfiguration(
                    configuration, DefaultRunExecutor.getRunExecutorInstance()
                )
            }
        })
    }

    companion object {
        val lessCommands = Vector(
            listOf(
                "deploy",
                "test",
                "sim",
                "coverage test",
                "coverage sim",
                "profiler test",
                "profiler sim",
                "run"
            )
        )
        val fullCommands =
            Vector(listOf("add-tests", "create-physics", "websim") + lessCommands)
    }
}
