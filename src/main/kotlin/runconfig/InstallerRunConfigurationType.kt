package runconfig

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import icons.PythonIcons


class InstallerRunConfigurationType :
    ConfigurationTypeBase(
        "ROBOTPY_INSTALLER_RUN_CONFIGURATION",
        "Robotpy Installer",
        "Robotpy installer command runner",
        PythonIcons.Python.Python
    ) {

    init {
        addFactory(InstallerRunConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): InstallerRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(InstallerRunConfigurationType::class.java)
    }
}
