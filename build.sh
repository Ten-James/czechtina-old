python3 utils/build.py

if [[ "$*" == *"--notest"* ]]; then
  echo "The --notest flag is present. Exiting..."
  exit 1
fi

build_and_run() {
  local c_file="$1"
  local binary_name="${c_file%.*}"  # Remove the file extension to get the binary name

  echo "Building $c_file"
  gcc "test/$c_file" -o "build/$binary_name"

  if [ -f "build/$binary_name" ]; then
    echo "Running $binary_name"
    "build/$binary_name"
  else
    echo "Error: Failed to build $binary_name"
  fi
}

build_and_run "t1.c"

build_and_run "t2.c"