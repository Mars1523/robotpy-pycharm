package runconfig

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import icons.PythonIcons

class RobotpyRunConfigurationType :
    ConfigurationTypeBase(
        "ROBOTPY_RUN_CONFIGURATION",
        "Robotpy",
        "Robotpy command runner configuration",
        PythonIcons.Python.Python
    ) {

    init {
        addFactory(RobotpyRunnerConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): RobotpyRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(RobotpyRunConfigurationType::class.java)
    }
}
