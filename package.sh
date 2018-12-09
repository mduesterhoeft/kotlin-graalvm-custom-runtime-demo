rm -rf build/package
mkdir build/package

cp build/server build/package

cp runtime/bootstrap build/package

zip -j build/package/bundle.zip build/package/bootstrap build/package/server