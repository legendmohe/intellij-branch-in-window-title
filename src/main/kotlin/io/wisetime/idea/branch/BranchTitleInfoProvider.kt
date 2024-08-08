package io.wisetime.idea.branch

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.impl.simpleTitleParts.SimpleTitleInfoProvider
import com.intellij.openapi.wm.impl.simpleTitleParts.TitleInfoOption

class BranchTitleInfoProvider : SimpleTitleInfoProvider(TitleInfoOption.ALWAYS_ACTIVE) {

    override fun getValue(project: Project): String {
        val service = project.getService(BranchHelper::class.java)
        if (service == null) {
            thisLogger().warn("Failed to get BranchHelper service")
            return ""
        }
        return service.getFormattedName(service.currentBranchName)
    }

    fun updateTitle() {
        updateNotify()
    }
}
