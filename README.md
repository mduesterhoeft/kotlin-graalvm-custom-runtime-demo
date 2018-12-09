# Kotlin serverless with GraalVm

This project demonstrates how to run a Kotlin serverless function on AWS lambda.
It uses a custom lambda runtime to be able to use GraalVM to run the function.
This avoids the increased latency when hitting a cold lambda.

To build and deploy install serverless and docker.

```
./package.sh
sls deploy
```
