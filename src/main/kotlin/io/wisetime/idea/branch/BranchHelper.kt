package io.wisetime.idea.branch

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class BranchHelper {
    var currentBranchName: String? = null

    fun getFormattedName(origin: String?): String {
        origin ?: ""
        return " [@$origin]"
    }
}
