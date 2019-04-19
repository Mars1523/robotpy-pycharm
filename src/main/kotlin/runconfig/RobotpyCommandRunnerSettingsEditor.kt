package runconfig

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.LabeledComponent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class RobotpyCommandRunnerSettingsEditor : SettingsEditor<RobotpyRunConfiguration>() {
    private var myPanel: JPanel? = null
    private var commandLabeledField: LabeledComponent<JTextField>? = null
    private var profileLabeledField: LabeledComponent<JCheckBox>? = null
    private var coverageLabeledField: LabeledComponent<JCheckBox>? = null

    override fun resetEditorFrom(robotpyRunConfiguration: RobotpyRunConfiguration) {
        commandLabeledField?.component?.text = robotpyRunConfiguration.command
        profileLabeledField?.component?.isSelected = robotpyRunConfiguration.profile
        coverageLabeledField?.component?.isSelected = robotpyRunConfiguration.coverage
    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(robotpyRunConfiguration: RobotpyRunConfiguration) {
        robotpyRunConfiguration.apply {
            command = commandLabeledField?.component?.text ?: ""
            profile = profileLabeledField?.component?.isSelected ?: false
            coverage = coverageLabeledField?.component?.isSelected ?: false
        }
    }

    override fun createEditor(): JComponent {
        return myPanel!!
    }

    private fun createUIComponents() {
        commandLabeledField = LabeledComponent()
        commandLabeledField!!.component = JTextField()
        profileLabeledField = LabeledComponent()
        profileLabeledField!!.component = JCheckBox()
        coverageLabeledField = LabeledComponent()
        coverageLabeledField!!.component = JCheckBox()
    }
}