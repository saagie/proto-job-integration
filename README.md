# POC - Job management
This POC consists on a simple library which will provide elementary functionalities to handle your jobs management.
This management is made by delegation, using one of our partner's software :
- Knime
- Trifacta
- Dataiku

## Content
### Modules
This little project can be splitted into three parts :
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

|**Concept**|**Dataiku**|**Trifacta**|**Knime**
|:-:|:-:|:-:|:-: 
|Project|Project| ---| Workflow
|Dataset|Dataset|WrangledDataset|---
|Job|Job|JobGroup|Job

#### Methods
Describes which methods are currently available for each app.

|**Functionnality**|**Dataiku**|**Trifacta**|**Knime**
|:-|:-:|:-:|:-: 
|Retrieve all projects | OK | --| OK 
|Retrieve all datasets for a given project | OK | OK | --
|Retrieve all jobs for a given project | OK | OK | OK 
| Retrieve a job with a specific ID | OK | OK | OK
| Retrieve a job's current status | OK | OK | OK
|Start a specific job| OK | OK | OK
|Stop a given job| OK| -- | --


## Demonstration tools
By using a correct spring profile, you can select which demonstration tool to use, for a rapid test of the functionalities :
- `demo` : Consists of an interactive demo which commands are described below.
- `starter` : An automatic execution of all library's methods, with a simple display of the results.
Note that it will require two additionnal parameters as environment variable (`PREDEFINED_PROJECT` and `PREDIFINED_JOB`) to function.

And to select your app, you can add (only one of them) : `dataiku`, `trifacta` or `knime`.

To modify the profile at launch, you should use a command like :
`java -Dspring.profiles.active=dataiku,demo -jar {YOUR_JAR}` 

*(In this example, we'd use the `demo` and `dataiku` profile)*


### Interactive demo
It includes the following commands :

**At any time**
- `projects` : Displays a list of all projects registered on the selected platform. (In Trifacta, as the *'project'* notion doesn't exist, only the value DEFAULT will be displayed.)
- `use $PROJECT` : Set which project to use for the next commands.

**After setting the project**
- `datasets` :  Displays a list of all datasets.
- `jobs` : Displays a list of all jobs and store their informations in order to manipulate them later. 

*Every job will be displayed with a specific number like : `$ID > $DESCRIPTION`, which is required by other commands.*

**After setting the project and getting all jobs**
- `status $ID` : Give the current status of the selected job
- `start $ID` : Starts the specified job.
- `stop $ID` : Stops the given job if currently running. Useless if the job's already done.

## Dev' setup
1) In **IntelliJ**, open `Run` > `Edit configurations`

2) Add a new Spring Boot configuration (+), and specify the main class of the app `io.saagie.poc.infra.AppKt`

3) *(Optional, if specified at launch)* Change the current app's action by changing the `Active profiles` attribute.

4) Update your environment varaibles as needed by your app in the `Override parameters` menu. 
*Most of the time,you'll only need to change the service's URL and define your credentials 
(Please check the `src/main/resources/application.yml` for the exact syntax).*

5) Run it through your IDE

You can also update your `application.yml` and directly build the app by using `mvn clean package`. 
You'll be able to launch the app directly by using the produced jar file.
