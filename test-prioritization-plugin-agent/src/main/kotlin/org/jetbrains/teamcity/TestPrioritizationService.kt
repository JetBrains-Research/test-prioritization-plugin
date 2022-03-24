package org.jetbrains.teamcity

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.util.TCStreamUtil
import java.io.File
import java.io.IOException


class TestPrioritizationService : BuildServiceAdapter() {
    private val myFilesToDelete: MutableSet<File> = HashSet()

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val message = "junit.jupiter.testmethod.order.default=org.example.CustomOrder\n" +
                "junit.jupiter.testclass.order.default=org.example.CustomClassOrder\n"
        val testsFolder = runnerParameters[PrioritizationConstants.TESTS_FOLDER_KEY]
        val folder = "$testsFolder/resources"
        val filename = "$folder/junit-platform.properties"
        val scriptContent = "mkdir -p '$folder' ; touch $filename ; /usr/bin/printf '$message' | tee '$filename'"
        val script = getCustomScript(scriptContent)
        setExecutableAttribute(script)
        return SimpleProgramCommandLine(runnerContext, script, emptyList())
    }

    private fun getCustomScript(scriptContent: String): String {
        try {
            val scriptFile = File.createTempFile("custom_script", ".sh", agentTempDirectory)
            FileUtil.writeFileAndReportErrors(scriptFile, scriptContent)
            myFilesToDelete.add(scriptFile)
            return scriptFile.absolutePath
        } catch (ioException: IOException) {
            val exception = RunBuildException(
                "Failed to create temporary custom script file in directory '$agentTempDirectory': $ioException",
                ioException
            )
            exception.isLogStacktrace = false
            throw exception
        }
    }

    private fun setExecutableAttribute(script: String) {
        try {
            TCStreamUtil.setFileMode(File(script), "a+x")
        } catch (t: Throwable) {
            throw RunBuildException("Failed to set executable attribute for custom script '$script'", t)
        }
    }

    override fun afterProcessFinished() {
        super.afterProcessFinished()
        for (file in myFilesToDelete) {
            FileUtil.delete(file)
        }
        myFilesToDelete.clear()
    }
}
