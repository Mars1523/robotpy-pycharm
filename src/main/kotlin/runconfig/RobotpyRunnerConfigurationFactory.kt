package runconfig

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project


class RobotpyRunnerConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return RobotpyRunConfiguration(project, this, "Robotpy")
    }

    override fun getName(): String {
        return FACTORY_NAME
    }

    companion object {
        const val FACTORY_NAME = "Robotpy configuration factory"
    }
}