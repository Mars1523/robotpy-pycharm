package runconfig

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class InstallerRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return InstallerRunConfiguration(project, this, "Robotpy Installer")
    }

    override fun getName(): String {
        return FACTORY_NAME
    }

    companion object {
        const val FACTORY_NAME = "Robotpy installer configuration factory"
    }
}