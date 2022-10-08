pipeline {
    agent any
    stages {
        stage('compile'){
            steps {
                sh 'echo stage1'
                git 'https://github.com/nareshdt99/PetClinic'
                sh '/opt/maven/bin/mvn compile'
            }    
        }
        stage('parallel_jobs'){
            parallel {
                stage('codereview'){
                    steps {
                        sh 'echo stage2'
                        sh '/opt/maven/bin/mvn -P metrics pmd:pmd'
                        }
                    post {
                        success{
                            recordIssues(tools: [pmdParser(pattern: '**/pmd.xml')])
                        }
                    }
                }    
                stage('unittest'){
                    steps {
                        sh 'echo stage3'
                        sh '/opt/maven/bin/mvn test'
                    }
                    post {
                        success {
                            junit 'target/surefire-reports/*.xml'
                        }
                    }
                }    
                stage('codecoverage'){
                    steps {
                        sh 'echo stage4'
                        sh '/opt/maven/bin/mvn cobertura:cobertura -Dcobertura.report.format=xml'
                    }
                    post {
                        success {
                            cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: 'target/site/cobertura/coverage.xml', conditionalCoverageTargets: '70, 0, 0', failUnhealthy: false, failUnstable: false, lineCoverageTargets: '80, 0, 0', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 0, 0', onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
                        }
                    }
                }     
            }
        }
        stage('package'){
            steps {
                sh 'echo stage5'
                sh '/opt/maven/bin/mvn package'  
            } 
        }
    }
            
}