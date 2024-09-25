/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 
import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate

JENKINSFILE_PATH = '.ci/jenkins'

// Release
setupKogitoDocsJob()


/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupKogitoDocsJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kie-docs-post-release', JobType.TOOLS, "${JENKINSFILE_PATH}/Jenkinsfile.kie-kogito-post-release", "Update Kie Kogito docs")
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
            JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
            GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
            GIT_AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
            GIT_AUTHOR_PUSH_CREDS_ID: "${GIT_AUTHOR_PUSH_CREDENTIALS_ID}",
            SSH_KOGITO_DOCS_PATH: "kogito@filemgmt-prod.jboss.org:/docs_htdocs/kogito/release",
            RSYNC_KOGITO_DOCS_PATH: "kogito@filemgmt-prod-sync.jboss.org:/docs_htdocs/kogito/release",
            KIE_DOCS_BRANCH: 'main-kogito',
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('KOGITO_DOCS_VERSION', '', 'Kogito docs version to release in kie-docs as Major.minor.micro')
            stringParam('KOGITO_RELEASE_NUMBER', '12409824', 'Jira release notes number of kogito release')
            stringParam('TOOLING_RELEASE_NUMBER', '12409093', 'Jira release notes number of kogito-tooling')
            stringParam('KOGITO_TOOLING_VERSION', '0.30.0', 'Kogito-tooling version to use as Major.minor.micro')
            booleanParam('BUMP_UP' , false, 'Please check only if a new KIE version needed')
            stringParam('KIE_VERSION' , '', 'KIE version to bump up to as Major.minor.micro-SNAPSHOT')
            booleanParam('SKIP_UPLOAD' , false, 'Please check only if the step for uploading the docs to filemgmt should be skipped')
        }
    }
}