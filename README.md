# CSV Report service

## How to run

### Using docker compose

```sh
cd <repo>
docker compose up
```

### Locally using maven

Make sure the following enviroment variables are set:

- `INPUT_FILE_PATH` - the path to the input csv file with incidents to parse from.
- `OUTPUT_FILE_PATH` - the path to the output csv file with incidents report.

Please prefer setting absolute paths contrary to the relative paths.
