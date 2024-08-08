package io.wisetime.idea.branch

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.impl.TitleInfoProvider
import com.intellij.project.stateStore
import git4idea.GitLocalBranch
import git4idea.branch.GitBranchUtil
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryChangeListener
import org.jetbrains.jps.util.JpsPathUtil
import java.util.*

// backgroundPostStartupActivity
class GitListener : StartupActivity {

    override fun runActivity(project: Project) {
        thisLogger().warn("runActivity() called with: project = ${project.name}")

        project.messageBus.connect()
            .subscribe(GitRepository.GIT_REPO_CHANGE, GitRepositoryChangeListener { repository: GitRepository ->
                val currentBranch = repository.currentBranch
                if (currentBranch != null) {
                    notifyBranchChanged(repository.project, currentBranch.name)
                }
            })
        ApplicationManager.getApplication().executeOnPooledThread {
            fetchAndUpdateTitle(project)
        }
    }

    private fun fetchAndUpdateTitle(project: Project) {
        Optional.ofNullable(GitBranchUtil.getRepositoryOrGuess(project, null))
            .map { obj: GitRepository ->
                thisLogger().warn("GitRepository = $obj")
                obj.currentBranch
            }
            .map { obj: GitLocalBranch? ->
                thisLogger().warn("GitLocalBranch = $obj")
                obj!!.name
            }
            .ifPresent { branchName: String ->
                notifyBranchChanged(project, branchName)
            }
    }

    private fun notifyBranchChanged(project: Project, branchName: String) {
        thisLogger().warn("notifyBranchChanged() called with: branchName = $branchName")
        val service = project.getService(BranchHelper::class.java)
        if (service == null) {
            thisLogger().warn("Failed to notify branch change")
            return
        }
        service.currentBranchName = branchName
        // fire update
        TitleInfoProvider.getProviders().filterIsInstance<BranchTitleInfoProvider>().onEach {
            it.updateTitle()
        }
        // update project name directly
        val newProjectName = project.stateStore.directoryStorePath?.let {
            JpsPathUtil.getDefaultProjectName(it)
        } ?: return
        (project as? ProjectEx)?.setProjectName(newProjectName + service.getFormattedName(branchName))
    }
}
