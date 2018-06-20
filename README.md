# POC - Job management
This POC consists on a simple library based on Dataiku's REST API and Trifacta's, which will provide elementary functionalities to handle your jobs management.


## Content
This little project can be splitted into three parts :
1) *The business logic* (`domain`) : Defines all informations required to define what a job is (`Job` = name, project, id, status), and
how we can manage it (`JobManager`).

2) *The implementation parts* (`infra`) : Contains all API requests and DTO to make it run with our job managers.

3) *The demo app* (`infra.App`) : A simple command line program to manipulate all available commands.


## Getting started
To select which platform you'd like to choose, you only need to update the `spring.profile.active` parameter
stored in the `/src/main/resources/application.yml`configuration file.
Currently, the available managers are `dataiku` and `trifacta`.

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

3) Select which Dataiku project to manipulate by changing the `project` parameter in the same configuration file.

4) Open a terminal at the project root, enter `mvn package`, and run the produced `.jar` file from
the freshly created `target` directory.

### Trifacta (`trifacta`)

> TO DO !! (beacuse WIP ;) )


## The demo tool

It includes the following commands :
- `all` : Displays a list of all jobs. **This must be your first command**, in order to load all informations about jobs.
Every job will be displayed with a specific number like : `ID > JOB_DESCRIPTION`, which is required by other commands.
- `status ID` : Give the current status of the selected job
- `start ID` : Starts the specified job.
- `stop ID` : Stops the given job if currently running. Useless if the job's already done.
