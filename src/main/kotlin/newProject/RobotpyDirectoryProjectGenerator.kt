package newProject

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.newProject.PyNewProjectSettings
import com.jetbrains.python.newProject.PythonProjectGenerator
import com.jetbrains.python.remote.PyProjectSynchronizer
import icons.PythonIcons
import javax.swing.Icon



class RobotpyDirectoryProjectGenerator : PythonProjectGenerator<PyNewProjectSettings>() {
    override fun configureProject(
        project: Project,
        baseDir: VirtualFile,
        settings: PyNewProjectSettings,
        module: Module,
        synchronizer: PyProjectSynchronizer?
    ) {
        super.configureProject(project, baseDir, settings, module, synchronizer)

        // Super simple class name sanitizer
        val robotName = baseDir.name.replace(Regex("^[0-9]|[^A-Za-z0-9_]"), "").capitalize()

        // Create project structure:
        //  * main file in robot/ dir
        //  * basic .gitignore
        //  * empty components/ dir
        ApplicationManager.getApplication().runWriteAction {
            val robotDir = baseDir.createChildDirectory(this, "robot")
            robotDir.createChildDirectory(this, "components")
            robotDir.createChildData(this, "robot.py").apply {
                setBinaryContent(getMainFileContent(robotName).toByteArray())
            }
            baseDir.createChildData(this, ".gitignore").apply {
                setBinaryContent(GITIGNORE_CONTENT.toByteArray())
            }
        }

        // Use robot.py to generate tests and physics file
        val mainFile = utils.findRobotFile(project) ?: return
        val pythonPath = settings.sdk?.homePath ?: return

        GeneralCommandLine(pythonPath, mainFile, "add-tests").createProcess()
        GeneralCommandLine(pythonPath, mainFile, "create-physics").createProcess()
    }

    override fun getName(): String {
        return "RobotPy MagicBot Robot"
    }

    override fun getLogo(): Icon? {
        return PythonIcons.Python.Python
    }

    override fun validate(p0: String): ValidationResult {
        return ValidationResult.OK
    }
}

private fun getMainFileContent(className: String): String {
    return """
            import wpilib
            import magicbot

            class $className(magicbot.MagicRobot):
                def createObjects(self):
                    pass

            if __name__ == "__main__":
                wpilib.run($className)
        """.trimIndent()
}

private const val GITIGNORE_CONTENT = """
*.py[codx]
__pycache__/
.pytest*/
.coverage
.mypy_cache/

# C extensions
*.so
*.dll

.deploy_cfg
/opkg_cache/
/pip_cache/
networktables.ini

.vscode/
.idea/
*.iml
*.iws
.DS_Store
"""