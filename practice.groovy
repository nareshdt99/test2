pipeline {
    agent any
    stages {
        stage('compile'){
            steps {
                sh 'echo step1'
                git 'https://github.com/nareshdt99/PetClinic'
                sh '/opt/maven/bin/mvn compile'
            }
       }
       stage('CodeReview'){
            steps {
                sh "echo step2"
                sh '/opt/maven/bin/mvn -P metrics pmd:pmd'
            }
            post {
                success {
                    recordIssues(tools: [pmdParser(pattern: '**/pmd.xml')])
                }
            }
       }
       stage('UnitTest'){
            steps {
                sh 'echo stage4'
                sh '/opt/maven/bin/mvn test'
            }
            post {
                success {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('CodeCoverage'){
            steps {
                sh 'echo stage5'
                sh '/opt/maven/bin/mvn cobertura:cobertura -Dcobertura.report.format=xml'
            }
            post {
                success {
                    cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: 'target/site/cobertura/coverage.xml', conditionalCoverageTargets: '70, 0, 0', failUnhealthy: false, failUnstable: false, lineCoverageTargets: '80, 0, 0', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 0, 0', onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
                }
            }
        }
        stage('Package'){
            steps{
                sh 'echo stage6'
                sh '/opt/maven/bin/mvn package'
            }
        }    
    }
}