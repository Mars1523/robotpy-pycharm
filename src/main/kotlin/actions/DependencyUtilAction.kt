package actions

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PopupAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import runconfig.InstallerRunConfiguration
import runconfig.InstallerRunConfigurationFactory
import runconfig.InstallerRunConfigurationType
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class DependencyUtilAction : AnAction(), PopupAction, ActionListener {
    private val packageField = ComboBox<String>(ROBOT_PACKAGES).apply {
        isEditable = true
        selectedItem = ""
    }
    private val pipRadio = JBRadioButton("Pip (python)")
    private val binaryRadio = JBRadioButton("Binary (opkg)")
    private val downloadButton = JButton("Download").also { b ->
        b.addActionListener(this)
        b.actionCommand = "download"
    }
    private val installButton = JButton("Install").also { b ->
        b.addActionListener(this)
        b.actionCommand = "install"
    }
    private val deployPanel = JPanel(GridLayout(2, 2, 30, 0))

    private var project: Project? = null


    init {
        pipRadio.isSelected = true
        ButtonGroup().apply {
            add(pipRadio)
            add(binaryRadio)
        }

        deployPanel.border = TitledBorder("Install")
        deployPanel.apply {
            add(pipRadio)
            add(downloadButton)
            add(binaryRadio)
            add(installButton)
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (e.project == null) {
            return
        }

        project = e.project!!

        packageField.selectedItem = ""

        val panel = FormBuilder()
            .addLabeledComponent("Package", packageField)
            .addComponent(deployPanel)
            .panel

        val ret = DialogBuilder(project).title("RobotPy Dependency Utility")
            .centerPanel(panel).resizable(false).show()

        val pkg = packageField.selectedItem as String
        if (ret == 0 && pkg.isBlank()) {
            Messages.showErrorDialog(project, "No package provided", "No Package")
        }
    }


    override fun actionPerformed(e: ActionEvent?) {
        val pkg = packageField.selectedItem as String
        val configuration =
            RunManager.getInstance(project ?: return)
                .createConfiguration(
                    "RobotPy Installer",
                    InstallerRunConfigurationFactory(InstallerRunConfigurationType.getInstance())
                )
        val installerConfig = configuration.configuration as InstallerRunConfiguration


        val suffix = if (binaryRadio.isSelected) {
            "opkg"
        } else {
            "pip"
        }
        when (e?.actionCommand) {
            "install" -> {
                installerConfig.command = "install-$suffix"
            }
            "download" -> {
                installerConfig.command = "download-$suffix"
            }
            else -> {
                println(e?.actionCommand)
                return
            }
        }
        installerConfig.arguments = pkg

        ProgramRunnerUtil.executeConfiguration(
            configuration, DefaultRunExecutor.getRunExecutorInstance()
        )
    }

    companion object {
        val ROBOT_PACKAGES = arrayOf(
            "navx",
            "ctre",
            "rev",
            "websim",
            "cscore",
            "pathfinder"
        ).map { "robotpy-$it" }.toTypedArray()
    }
}

