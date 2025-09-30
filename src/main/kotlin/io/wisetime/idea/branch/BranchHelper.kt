package io.wisetime.idea.branch

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.project.stateStore
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

@Service(Service.Level.PROJECT)
class BranchHelper {
    var currentBranchName: String? = null

    fun getFormattedName(origin: String?): String {
        origin ?: ""
        return " [@$origin]"
    }

    fun ensureNameFile(project: Project, branchName: String) {
        val resolve = project.stateStore.directoryStorePath?.resolve(".name") ?: return
        val nameFile = resolve.toFile()
        try {
            // TODO - 这里会不断增长！要想办法拿到最原始的projectName
            val originalName = getOriginalProjectName(project)
            val expectedName = "$originalName${getFormattedName(branchName)}"
            if (!nameFile.exists()) {
                nameFile.writeText(expectedName)
            } else {
                if (nameFile.readText() != expectedName) {
                    nameFile.writeText(expectedName)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getOriginalProjectName(project: Project): String? {
        // 方法1: 通过项目文件路径获取
        val basePath = project.basePath
        if (basePath != null) {
            val path: Path = Paths.get(basePath)
            return path.fileName.toString()
        }
        return null
    }
}
