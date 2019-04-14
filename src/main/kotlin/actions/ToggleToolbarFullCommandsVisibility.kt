package actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.ui.UIUtil
import toolwindow.RobotpyToolWindowPanel

class ToggleToolbarFullCommandsVisibility : ToggleAction() {
    var showFull = false

    override fun isSelected(e: AnActionEvent): Boolean = showFull

    override fun setSelected(e: AnActionEvent, selected: Boolean) {
        showFull = selected

        val toolWindowComponent =
            ToolWindowManager.getInstance(
                e.project ?: return
            ).getToolWindow("RobotPy").component
        val toolWindow = UIUtil.findComponentOfType(toolWindowComponent, RobotpyToolWindowPanel::class.java)

        if (showFull) {
            toolWindow?.showFullCommands()
        } else {
            toolWindow?.showLessCommands()
        }
    }
}