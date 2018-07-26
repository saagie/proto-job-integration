
# POC - Job management 

This POC consists on a simple library which will provide elementary functionalities to handle your jobs management.    
This management is made by delegation, using one of our partner's software :    
- Knime    
- Trifacta    
- Dataiku    
- Nifi  
- AWS Glue
    
## Content 
### Modules 
This project can be logically splitted into three parts :  
  
1) *The business logic* (`domain`) : Defines all informations required to define what a job is (`Job` = name, project, id, status), and    
how we can manage it (`JobManager`).    
    
2) *The implementation parts* (`infra.right`) : Contains all API requests and DTO to make it run with our job managers.    
    
3) *The demo apps* (`infra.left`) : Simple apps to manipulate all available commands.    
    
### Concepts    
 - **Jobs** : A Job is a specific action which will produces an output result on a given data input.    
- **Dataset** : A dataset is used to store a specified quantity of data. A dataset is usually created from an import process,    
or as the result of a given jobs's execution. *(In some apps, jobs are related to datasets as their source.)* 
- **Project** : A project is a group of datasets and jobs, and describes how they're related on to another, in order to produce    
the solution to given problem.    
    
### Compatibility 
#### Concepts 
Describes the matching between a given app's concepts and ours.    
    
|Concept|Dataiku|Trifacta|Knime|Nifi|AWS Glue
|:-:|:-:|:-:|:-:|:-:|:-:
|Project|Project| ---| Workflow| ProcessGroup| ---    
|Dataset|Dataset|WrangledDataset|---|---|---    
|Job|Job|JobGroup|Job| Processor | Job
    
#### Methods 
Describes which methods are currently available for each app.    
    
|Functionnality|Dataiku|Trifacta|Knime|Nifi|AWS Glue  
|:-|:-:|:-:|:-:|:-:|:-:
|Retrieve all projects | OK | --| OK | OK | --
|Retrieve all datasets for a given project | OK | OK | -- | -- | --
|Retrieve all jobs for a given project | OK | OK | OK | OK | OK 
|Retrieve a job with a specific ID | OK | OK | OK | OK | OK 
|Retrieve a job's current status | OK | OK | OK | OK | OK 
|Start a specific job| OK | OK | OK | OK | OK 
|Stop a given job| OK | -- | -- | OK | OK 
|Import a job| -- | -- | OK | OK | -- 
|Export a job| -- | OK | OK | OK | OK 
|Import a project| -- | -- | -- | OK | --
|Export a project| -- | -- | -- | OK | --
|Security profile|`basic`|`basic`|`basic`|`none`|`none`*

*Securization base on a specific system, not by the use of an option decided by profile at runtime.
    
## Demonstration tools 

By using a correct spring profile, you can select which demonstration tool to use, for a rapid test of the functionalities :    
- `demo` : Consists of an interactive demo which commands are described below.    
- `starter` : An automatic execution of all library's methods, with a simple display of the results.    
Note that it will require two additionnal parameters as environment variable (`PREDEFINED_PROJECT` and `PREDIFINED_JOB`) to function.    
    
And to select your app, you can add (only one of them) : `dataiku`, `trifacta` or `knime`.    
    
To modify the profile at launch, you should use a command like :    
`java -Dspring.profiles.active=dataiku,demo -jar {YOUR_JAR}` *(In this example, we'd use the `demo` and `dataiku` profile)*    
    
 ### Interactive demo    
 *In this demo, every job or project will be displayed with a specific number like : `$ID : $OBJECT`.    
 This number will define a local ID, which is required by the local demo tool to realize some of the other functionalities*    
 **At any time** - `projects` : Displays a list of all projects registered on the selected platform. (In Trifacta, as the *'project'* notion doesn't exist, only the value DEFAULT will be displayed.)    
  
**After setting the project** - `use $PROJECT_ID` : Set which project to use for the next commands.    
- `download $PROJECT_ID` : Retrieve the selected project as a JSON file ready to be imported elsewhere.  
- `upload $PROJECT_ID` : Create a new project based on the previously downloaded project. *Only usable after a first download !*  
- `datasets` :  Displays a list of all datasets.    
- `jobs` : Displays a list of all jobs and store their informations in order to manipulate them later.     
    
**After setting the project and getting all jobs** - `status $JOB_ID` : Give the current status of the selected job    
- `start $JOB_ID` : Starts the specified job.    
- `stop $JOB_ID` : Stops the given job if currently running. *Useless if the job's already done.*  
- `export $JOB_ID` : Retrieve the selected job as a JSON file ready to be imported in an other project.  
- `import $JOB_ID` : Create a new job based on the previously exported job. *Only usable after a first job export !*  
  
## Dev' setup **KISS**  
  
1) In **IntelliJ**, open `Run` > `Edit configurations`    
 2) Add a new Spring Boot configuration (+), and specify the main class of the app `io.saagie.poc.infra.AppKt`    
 3) *(Optional, if specified at launch)* Change the current app's action by changing the `Active profiles` attribute.    
    
4) Update your environment varaibles as needed by your app in the `Override parameters` menu. :  
- a)   
*Most of the time,you'll only need to change the service's URL   
(Please check the `src/main/resources/application.yml` for the exact syntax).*  
  
- b) Select with a profile argument (`spring.profile.active`), which kind of security to use :  
  - `none` : No security, I hope you know what you're doing :)  
  - `basic` : You'll have to inform your username and password under the `common` section of the `application.yml`  
 - `token` : You'll have to inform your URL under the `common` section of the `application.yml`,  
and eventually your (username, password) if the token's request is secured with basic auth.  
    
5) Run it through your IDE.  
    
**Build and run**  
  
Build the app by using `mvn clean package`, and then edit the provided utility script `start.sh`, by adding/updating, all environment variables to overload before executing the jar.   
  
Then, you'll be able to launch the app by using `./start.sh $PATH_TO_YOUR_JAR`.
