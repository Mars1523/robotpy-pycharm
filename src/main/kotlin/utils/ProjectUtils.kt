package utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager

fun getPythonPath(project: Project): String? {
    return ProjectRootManager.getInstance(project).projectSdk?.homePath
}

fun findRobotFile(project: Project): String? {
    val projectRootManager = ProjectRootManager.getInstance(project)

    val sourceRoot = projectRootManager.contentRoots.first()
    val mainFile = sourceRoot.findChild("robot")?.findChild("robot.py")
        ?: return null
    return mainFile.canonicalPath
}
