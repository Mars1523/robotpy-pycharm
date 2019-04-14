package runconfig

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import javax.swing.JComponent
import javax.swing.JPanel

class RobotpyCommandRunnerSettingsEditor : SettingsEditor<RobotpyRunConfiguration>() {
    private var myPanel: JPanel? = null
    private var myMainClass: LabeledComponent<ComponentWithBrowseButton<*>>? = null

    override fun resetEditorFrom(robotpyRunConfiguration: RobotpyRunConfiguration) {

    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(robotpyRunConfiguration: RobotpyRunConfiguration) {

    }

    override fun createEditor(): JComponent {
        return myPanel!!
    }

    private fun createUIComponents() {
        myMainClass = LabeledComponent()
        myMainClass!!.component = TextFieldWithBrowseButton()
    }
}