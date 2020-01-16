package runconfig

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.filters.UrlFilter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScopes
import com.jetbrains.python.PythonHelpersLocator
import com.jetbrains.python.console.PythonDebugLanguageConsoleView
import com.jetbrains.python.run.PythonTracebackFilter
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import org.jdom.Element
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import utils.xml.readBool
import utils.xml.readString
import utils.xml.writeBool
import utils.xml.writeString
import java.nio.file.Paths

private class RobotpyRunState(
    val config: RunConfiguration,
    val environment: ExecutionEnvironment,
    private val command: List<String>
) :
    RunProfileState {

    val project = environment.project
    var plainConsoleBuilder: TextConsoleBuilder = run {
        val searchScope = GlobalSearchScopes.executionScope(project, environment.runProfile)
        TextConsoleBuilderFactory.getInstance().createBuilder(project, searchScope)
    }

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        val cmd = GeneralCommandLine(command)

        val test = command.contains("test")
        if (test) {
            cmd.addParameters("--", "-p", "pytest_teamcity")
            cmd.environment["PYTHONPATH"] = PythonHelpersLocator.getHelperPath("pycharm")
        }

        val processHandler = OSProcessHandler(cmd)

        val console = if (test) {
            createTestConsole(processHandler)
        } else {
            createExecutionConsole(processHandler)
        }

        return DefaultExecutionResult(console, processHandler, *AnAction.EMPTY_ARRAY)
    }

    fun createExecutionConsole(processHandler: ProcessHandler): ConsoleView {
        return plainConsoleBuilder.console.apply {
            attachToProcess(processHandler)
        }
    }

    fun createTestConsole(processHandler: ProcessHandler): PythonDebugLanguageConsoleView {
        val props = SMTRunnerConsoleProperties(config, "pytest", environment.executor)

        val testsOutputConsoleView = SMTestRunnerConnectionUtil.createAndAttachConsole("pytest", processHandler, props)

        val mainFilePath = utils.findRobotFile(project) ?: throw ExecutionException("Unable to resolve main file path")

        val console = PythonDebugLanguageConsoleView(
            project, PythonSdkUtil.findSdkByPath(utils.getPythonPath(project)),
            testsOutputConsoleView, true
        )

        console.addMessageFilter(PythonTracebackFilter(project, Paths.get(mainFilePath).parent.toString()))
        console.addMessageFilter(UrlFilter())

        return console
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
        val mainFilePath = utils.findRobotFile(project) ?: throw ExecutionException("Unable to resolve main file path")
        val pythonPath =
            utils.getPythonPath(project) ?: throw ExecutionException("Unable to detect python executable")

        LOG.info("Executing robotpy command: \"$command\" with robot path \"$mainFilePath\" [$pythonPath]")

        val cmd = mutableListOf(pythonPath, mainFilePath)
        if (profile) {
            cmd += "profiler"
        } else if (coverage) {
            cmd += "coverage"
        }
        cmd += command

        return RobotpyRunState(this, executionEnvironment, cmd)
    }
}
