// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

package _self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object BuildLearningStandardsClientCli : BuildType ({
    name = "Build Ed-Fi Learning Standards CLI"

    publishArtifacts = PublishMode.SUCCESSFUL
    enablePersonalBuilds = false
    artifactRules = """
        src\EdFi.Admin.LearningStandards.Core\bin\%buildConfiguration%\*.nupkg =>
        src\EdFi.Admin.LearningStandards.CLI\bin\%buildConfiguration%\net6.0\win-x64\publish => EdFi.Admin.LearningStandards.CLI.win-x64.zip
    """.trimIndent()
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("env.Git_Branch", "%teamcity.build.branch%")
        param("PackageVersion", "%learningStandardsClient.version.major%.%learningStandardsClient.version.minor%.%build.counter%")
        param("buildConfiguration", "Release")
    }

    triggers {
        vcs {
            id ="vcsTrigger"
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_CUSTOM
            quietPeriod = 120
            branchFilter = """
                +:*
            """.trimIndent()
        }
    }

    steps {
        exec {
            name = "GitVersion"
            enabled = false
            path = "GitVersion.exe"
            arguments = "/output buildserver"
        }
        dotnetBuild {
            name = "Build Solution"
            projects = "src/EdFi.Admin.LearningStandards.sln"
            configuration = "%buildConfiguration%"
            args = "/p:Version=%PackageVersion%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        powerShell {
            name = "Execute tests"
            formatStderrAsError = true
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
            scriptMode = script {
                content = """
                    dotnet test 'src/EdFi.Admin.LearningStandards.Tests/EdFi.Admin.LearningStandards.Tests.csproj'
                """.trimIndent()
            }
        }
        dotnetPack {
            name = "Package LS Core Assembly"
            projects = "src/EdFi.Admin.LearningStandards.Core/EdFi.Admin.LearningStandards.Core.csproj"
            configuration = "%buildConfiguration%"
            skipBuild = true
            args = "--include-symbols /p:Version=%PackageVersion%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPublish {
            name = "Publish Winddows CLI"
            projects = "src/EdFi.Admin.LearningStandards.CLI/EdFi.Admin.LearningStandards.CLI.csproj"
            framework = "net6.0"
            configuration = "%buildConfiguration%"
            runtime = "win-x64"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    features {
        swabra {
            forceCleanCheckout = true
        }
    }
})
