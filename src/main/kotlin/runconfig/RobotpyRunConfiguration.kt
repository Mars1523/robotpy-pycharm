package runconfig

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import org.jdom.Element
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


private class RobotpyRunState(environment: ExecutionEnvironment, private val command: List<String>) :
    CommandLineState(environment) {
    public override fun startProcess(): ProcessHandler {
        val cmd = OSProcessHandler(GeneralCommandLine(command))
        ProcessTerminatedListener.attach(cmd)
        return cmd
    }
}

class RobotpyRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfiguration>(project, factory, name) {
    private val LOG = Logger.getInstance(this::class.java)

    var command: String = ""
    var coverage = false
    var profile = false

    override fun readExternal(element: Element) {
        super.readExternal(element)
        element.readString("command")?.let { command = it }
        element.readBool("profile")?.let { profile = it }
        element.readBool("coverage")?.let { coverage = it }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.writeString("command", command)
        element.writeBool("profile", profile)
        element.writeBool("coverage", coverage)
    }

    @NotNull
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RobotpyCommandRunnerSettingsEditor()
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {

    }

    @Nullable
    @Throws(ExecutionException::class)
    override fun getState(@NotNull executor: Executor, @NotNull executionEnvironment: ExecutionEnvironment): RunProfileState? {
        val projectRootManager = ProjectRootManager.getInstance(project)

        val sourceRoot = projectRootManager.contentRoots.first()
        val mainFile = sourceRoot.findChild("robot")?.findChild("robot.py")
            ?: throw ExecutionException("Unable to find robot/robot.py")
        val mainFilePath = mainFile.canonicalPath ?: throw ExecutionException("Unable to resolve main file path")
        val pythonPath =
            projectRootManager.projectSdk?.homePath ?: throw ExecutionException("Unable to detect python executable")

        LOG.info("Executing robotpy command: \"$command\" with robot path \"$mainFilePath\" [$pythonPath]")

        var cmd = listOf(pythonPath, mainFilePath)
        if (profile) {
            cmd += "profiler"
        } else if (coverage) {
            cmd += "coverage"
        }
        cmd += command

        return RobotpyRunState(executionEnvironment, cmd)
    }
}


private fun Element.writeString(name: String, value: String) {
    val opt = org.jdom.Element("option")
    opt.setAttribute("name", name)
    opt.setAttribute("value", value)
    addContent(opt)
}

private fun Element.readString(name: String): String? =
    children
        .find { it.name == "option" && it.getAttributeValue("name") == name }
        ?.getAttributeValue("value")

private fun Element.writeBool(name: String, value: Boolean) {
    writeString(name, value.toString())
}

private fun Element.readBool(name: String): Boolean? =
    readString(name)?.toBoolean()

