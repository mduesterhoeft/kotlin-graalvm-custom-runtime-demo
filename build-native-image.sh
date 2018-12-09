./gradlew shadowJar

$GRAAL_HOME/bin/native-image --enable-url-protocols=http \
  -H:+ReportUnsupportedElementsAtRuntime --no-server -jar build/libs/http4k-serverless-custom-runtime-1.0-all.jar

mv http4k-serverless-custom-runtime-1.0-all build/server