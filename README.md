# AWS Lambda with Kotlin and GraalVM

This project demonstrates how to run a Kotlin serverless function on AWS lambda using GraalVM
It takes advantage of the  [custom lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html) to be able to use GraalVM to run the function.
This avoids the increased latency on cold startup.

For more information read the post belonging to this repo - https://medium.com/@mathiasdpunkt/fighting-cold-startup-issues-for-your-kotlin-lambda-with-graalvm-39d19b297730

Run the `package.sh` to create a deployable zip file. This script does the following:

- build a fat jar of the application
- use the `oracle/graalvm-ce` docker image to build a native image
- package the native image together with the [runtime/bootstrap](bootstrap) script into `build/package/bundle.zip`

We are using the [serverless framework](https://serverless.com/) to deploy the function.

So to package and deploy run:

```
npm install
./package.sh
npx serverless deploy
```

To call the endpoint run:

```
curl  https://<your-api-id-here>.execute-api.eu-central-1.amazonaws.com/dev/greet/some
```

After calling the function we can look at the logs to get information about the execution time.

```
npx serverless logs -f hello
#REPORT RequestId: 155aa77e-6f07-4f31-a468-6de06edfd98b  Duration: 23.85 ms      Billed Duration: 300 ms Memory Size: 1024 MB    Max Memory Used: 66 MB  Init Duration: 178.22 ms     
```
