package runconfig

import com.intellij.openapi.options.*
import com.intellij.openapi.ui.*

import javax.swing.*

class RobotpyCommandRunnerSettingsEditor : SettingsEditor<RobotpyRunConfiguration>() {
    private var myPanel: JPanel? = null
    private var myMainClass: LabeledComponent<ComponentWithBrowseButton<*>>? = null

    override fun resetEditorFrom(robotpyRunConfiguration: RobotpyRunConfiguration) {

    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(robotpyRunConfiguration: RobotpyRunConfiguration) {
//        robotpyRunConfiguration.settings.copy(command = this.myMainClass?.text ?: "help")

    }

    override fun createEditor(): JComponent {
        return myPanel!!
    }

    private fun createUIComponents() {
        myMainClass = LabeledComponent()
        myMainClass!!.component = TextFieldWithBrowseButton()
    }
}