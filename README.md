# POC - Job management
This POC consists on a simple library based on Dataiku's REST API and Trifacta's, which will provide elementary functionalities to handle your jobs management.


## Content
This little project can be splitted into three parts :
1) *The business logic* (`domain`) : Defines all informations required to define what a job is (`Job` = name, project, id, status), and
how we can manage it (`JobManager`).

2) *The implementation parts* (`infra.right`) : Contains all API requests and DTO to make it run with our job managers.

3) *The demo apps* (`infra.left`) : Simple apps to manipulate all available commands.


## Demonstration tools
By using a correct spring profile, you can select which demonstration tool to use, for a rapid test of these functionalities :
- `demo` : Consists of an interactive demo which commands are described below.
- `starter` : An automatic execution of all library's methods, with a simple display of the results.

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


## Dev's Getting started
To select which platform you'd like to choose, you only need to update the `spring.profile.active` parameter
stored in the `/src/main/resources/application.yml`configuration file.
Currently, the available managers are `dataiku` and `trifacta`. Their configuration relies on environment variables, which
you'll need to alter to your own needs.

### Dataiku (`dataiku`)
#### Ready-to-go setup
Currently, the app is pre-defined to work on A5 dev platform, by using my trial's API key.
You can directly use the `.jar`, if you already have an access to this service.
A few jobs have been defined to be used during the demo (Project name : `FIRSTTRY`)

#### Custom setup
1) Setup your Dataiku DSS's informations on configuration file `/src/main/resources/application.yml`. 
You'll only have to change the `host` and the `port` properties.

2) Retrieve your API token from Dataiku ([see Documentation above](https://doc.dataiku.com/dss/latest/publicapi/keys.html))
and change the `apikey` parameter in the `/src/main/resources/application.yml` file.

3) Select which Dataiku project and job to manipulate (in the starter) by changing the `project` and `job` parameters 
in the `common` part of the same configuration file.

4) Open a terminal at the project root, enter `mvn package`, and run the produced `.jar` file from
the freshly created `target` directory.

### Trifacta (`trifacta`)
#### Ready-to-go setup
Currently, the app is deployed on Saagie's demo platform under the name `Poc - Trifacta`.
You can simply run it and read the incoming logs to show some results.

#### Custom setup
1) Setup your Trifacta's informations on configuration file `/src/main/resources/application.yml`. 
You'll only have to change the base URL, the username and password.

2) Select which job to manipulate by changing the `project` parameter in the `common` section of the same configuration file.
