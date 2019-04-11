package runconfig

import com.intellij.execution.configurations.*
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import icons.PythonIcons


class RobotpyRunConfigurationType :
    ConfigurationTypeBase(
        "ROBOTPY_RUN_CONFIGURATION",
        "Robotpy",
        "Robotpy command runner configuration",
        PythonIcons.Python.InterpreterGear
    ) {

    init {
        addFactory(RobotpyRunnerConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): RobotpyRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(RobotpyRunConfigurationType::class.java)
    }
}

//class RobotpyRunConfigurationType : ConfigurationType {
//    override fun getDisplayName(): String {
//        return "Robotpy"
//    }
//
//    override fun getConfigurationTypeDescription(): String {
//        return "Robotpy Command Runner"
//    }
//
//    override fun getIcon(): Icon {
//        return AllIcons.General.Information
//    }
//
//    override fun getId(): String {
//        return "ROBOTPY_RUN_CONFIGURATION"
//    }
//
//    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
//        return arrayOf(RobotpyRunnerConfigurationFactory(this))
//    }
//}