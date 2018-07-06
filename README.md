API Simulator
=============

Example of non-trivial API.

### Unit tests
```
sbt test
```

### Integration tests
```
sbt it:test
```

### Examples of how to test the HTTP requests available using `Curl`:

#### POST /user/processBytes

##### testing the local environment
```
curl -v -X POST -H "Accept: application/vnd.hmrc.1.0+json" --data-binary @"<FILE_PATH>" http://localhost:8103/user/processBytes
curl -v -X POST -H "Accept: application/vnd.hmrc.1.0+json" --data "Hello mate"          http://localhost:8103/user/processBytes
```

##### testing the staging environment, through the `curl-microservice` Jenkins job
- Go to this URL (using Jenkins Orchestrator in `staging`: `http://JENKINS_URL/user/<JENKINS_USR>/configure`
- Follow the steps defined here: http://stackoverflow.com/questions/34632796/jenkins-trigger-a-job-from-api
- Run this Bash command:
```
curl -X POST  \
       -H "Cache-Control: no-cache"  \
       -H "Content-Type: application/x-www-form-urlencoded"  \
       -d 'ARGS= -v -X POST -H "Accept: application/vnd.hmrc.1.0+json" -H "Content-Type: application/json" --data "Hello mate" http://<API_SIMULATOR_HOST>/user/processBytes'  \
       "https://<JENKINS_USR>:<JENKINS_API_TOKEN | JENKINS_PWD>@<JENKINS_HOSTNAME>/job/curl-microservice/buildWithParameters"
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
