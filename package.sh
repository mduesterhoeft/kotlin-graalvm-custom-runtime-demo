./gradlew shadowJar

docker run --rm --name graal -v $(pwd):/working oracle/graalvm-ce:1.0.0-rc10 \
    /bin/bash -c "native-image --enable-url-protocols=http \
                    -Djava.net.preferIPv4Stack=true \
                    -H:ReflectionConfigurationFiles=/working/reflect.json \
                    -H:+ReportUnsupportedElementsAtRuntime --no-server -jar /working/build/libs/kotlin-graalvm-custom-runtime-demo-1.0-all.jar \
                    ; \
                    cp kotlin-graalvm-custom-runtime-demo-1.0-all /working/build/server"

rm -rf build/package
mkdir build/package

cp build/server build/package

cp runtime/bootstrap build/package

zip -j build/package/bundle.zip build/package/bootstrap build/package/server