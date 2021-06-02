# STEP 2020 Capstone project

Link to the Website: https://engage-step.appspot.com/

Our objective is to provide users with relevant news and action-initiatives based on their interests We want users to be able to deeply understand topics related to their interests (as indicated by their social media activity) and have the resources to take action.

## How To Run

### Setting your default Java version to Java 8
Run the following comand before trying to run the application:
`sudo update-java-alternatives -s java-1.8.0-openjdk-amd64 && export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre`

To permanently switch to Java 8, add the command to your .bashrc file.

### Running the app
For security purposes, the API keys used in this application must be passed as command line arguments.
To run, use the following command: 	`[mvn command] -DgoogleKey=[Key for Google APIs] -DprojectsKey=[Global Giving API Key]`
