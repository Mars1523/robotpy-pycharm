// TODO: Find a way to reduce duplication
package runconfig

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import org.jdom.Element
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import utils.xml.readString
import utils.xml.writeString

private class RobotpyInstallerRunState(environment: ExecutionEnvironment, private val command: List<String>) :
    CommandLineState(environment) {
    public override fun startProcess(): ProcessHandler {
        val cmd = OSProcessHandler(GeneralCommandLine(command))
        ProcessTerminatedListener.attach(cmd)
        return cmd
    }
}

class InstallerRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfiguration>(project, factory, name) {
    var command = ""
    var arguments = ""
    var team = ""
    var hostname = ""

    override fun readExternal(element: Element) {
        super.readExternal(element)
        element.readString("command")?.let { command = it }
        element.readString("arguments")?.let { arguments = it }
        element.readString("team")?.let { team = it }
        element.readString("hostname")?.let { hostname = it }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.writeString("command", command)
        element.writeString("arguments", arguments)
        element.writeString("team", team)
        element.writeString("hostname", hostname)
    }

    @NotNull
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return InstallerRunSettingsEditor()
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
    }

    @Nullable
    @Throws(ExecutionException::class)
    override fun getState(@NotNull executor: Executor, @NotNull executionEnvironment: ExecutionEnvironment): RunProfileState? {
        val projectRootManager = ProjectRootManager.getInstance(project)

        val pythonPath =
            projectRootManager.projectSdk?.homePath ?: throw ExecutionException("Unable to detect python executable")

        val cmd = mutableListOf(pythonPath, "-m", "robotpy_installer", command)

        if (team.isNotEmpty()) {
            cmd += "--team"
            cmd += team
        }
        if (hostname.isNotEmpty()) {
            cmd += "--robot"
            cmd += hostname
        }

        val tokenizer = CommandLineTokenizer(arguments, true)
        val args = mutableListOf<String>()
        while (tokenizer.hasMoreTokens()) {
            args.add(tokenizer.nextToken())
        }
        cmd.addAll(args)

        return RobotpyInstallerRunState(executionEnvironment, cmd)
    }
}
