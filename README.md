# AWS lambda with Kotlin and GraalVM

This project demonstrates how to run a Kotlin function on AWS Lambda using GraalVM.
It takes advantage of the  [custom lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html) to run a native machine image created by GraalVM.
Running a native image reduces the cold startup times significantly.

Run the `package.sh` to create a deployable zip file. This script does the following:

- build a fat jar of the application
- use the `oracle/graalvm-ce` docker image to build a native image
- package the native image together with the [runtime/bootstrap](bootstrap) script into `build/package/bundle.zip`

We are using the [serverless framework](https://serverless.com/) to deploy the function.

So to package and deploy run:

```
./package.sh && sls deploy
```
