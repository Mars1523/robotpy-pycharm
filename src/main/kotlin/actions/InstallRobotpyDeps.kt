package actions

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.packaging.PyPackageUtil
import com.jetbrains.python.psi.LanguageLevel

class InstallRobotpyDeps : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val projectRootManager = ProjectRootManager.getInstance(e.project ?: return)
        val sdk = projectRootManager.projectSdk ?: return
        val pypkg = PyPackageManager.getInstance(sdk)

        val x = Messages.showOkCancelDialog(
            e.project!!,
            "Install pyfrc?",
            "Install Dependencies?",
            "Ok",
            "Cancel",
            AllIcons.General.QuestionDialog
        )
        if (x == 0) {
            // Install pyfrc and make sure user has wpilib in requirements.txt
            ProgressManager.getInstance().run(object : Task.Backgroundable(e.project, "Installing packages") {
                override fun run(pi: ProgressIndicator) {
                    pi.isIndeterminate = true
                    WriteCommandAction.runWriteCommandAction(e.project!!) {
                        val mod = ModuleUtilCore.findModuleForFile(projectRootManager.contentRoots.first(), e.project!!)

                        val hasWpilib = PyPackageUtil.getRequirementsFromTxt(mod!!)
                            ?.contains(PyPackageManager.getInstance(sdk).parseRequirement("wpilib")) ?: false
                        if (!hasWpilib) {
                            PyPackageUtil.addRequirementToTxtOrSetupPy(mod, "wpilib", LanguageLevel.PYTHON37)
                        }
                    }
                    val completer = java.util.concurrent.CountDownLatch(1)
                    ApplicationManager.getApplication().executeOnPooledThread {
                        pi.text = "Installing pyfrc"
                        pypkg.install("pyfrc")
                        completer.countDown()
                    }
                    completer.await()
                    Notifications.Bus.notify(Notification("Pyfrc Installation", "Pyfrc Installation", "Pyfrc Successfully Installed", NotificationType.INFORMATION))
                }
            })
        }
    }
}