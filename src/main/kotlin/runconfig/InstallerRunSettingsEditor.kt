package runconfig

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.LabeledComponent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class InstallerRunSettingsEditor : SettingsEditor<InstallerRunConfiguration>() {
    private var myPanel: JPanel? = null
    private var commandLabeledField: LabeledComponent<JTextField>? = null
    private var argumentsLabeledField: LabeledComponent<JTextField>? = null

    override fun resetEditorFrom(installerRunConfiguration: InstallerRunConfiguration) {
        commandLabeledField?.component?.text = installerRunConfiguration.command
        argumentsLabeledField?.component?.text = installerRunConfiguration.arguments
    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(installerRunConfiguration: InstallerRunConfiguration) {
        installerRunConfiguration.apply {
            command = commandLabeledField?.component?.text ?: ""
            arguments = argumentsLabeledField?.component?.text ?: ""
        }
    }

    override fun createEditor(): JComponent {
        return myPanel!!
    }

    private fun createUIComponents() {
        commandLabeledField = LabeledComponent()
        commandLabeledField!!.component = JTextField()
        argumentsLabeledField = LabeledComponent()
        argumentsLabeledField!!.component = JTextField()
    }
}