# Storage API Documentation

This section describes the API of the `Storage` component.

The `Storage` component is responsible for persisting review data to disk and loading saved data back into the application at startup. It serves as the bridge between the in-memory model (`ReviewList`) and the text file used for long-term storage.

The component supports:

* loading reviews from a storage file,
* saving reviews to a storage file,
* returning non-fatal warnings when malformed stored data is encountered.

During loading, the storage component performs **best-effort parsing**. This means that if some review blocks in the file are malformed, those invalid blocks may be skipped while valid review blocks are still loaded successfully.

---

## `Storage()`

### Description

Constructs a `Storage` object that uses the default storage file path, `data/reviews.txt`.

### Parameters / Inputs

None.

### Return Value

| Type      | Description                                            |
| --------- | ------------------------------------------------------ |
| `Storage` | A `Storage` object using the default storage location. |

### Example Usage

```java
Storage storage = new Storage();
```

---

## `Storage(Path storageFilePath)`

### Description

Constructs a `Storage` object with a custom file path.

This is useful when the application needs to use a non-default storage location, such as during testing.

### Parameters / Inputs

| Parameter         | Type   | Description                          |
| ----------------- | ------ | ------------------------------------ |
| `storageFilePath` | `Path` | The path of the storage file to use. |

### Return Value

| Type      | Description                                       |
| --------- | ------------------------------------------------- |
| `Storage` | A `Storage` object using the specified file path. |

### Example Usage

```java
Storage storage = new Storage(Paths.get("data", "reviews.txt"));
```

---

## `loadReviews()`

### Description

Loads reviews from the storage file and returns them as a `ReviewList`.

If the storage file or its parent directories do not yet exist, they are created automatically.

This method is suitable when only the loaded review data is needed.

### Parameters / Inputs

None.

### Return Value

| Type         | Description                                       |
| ------------ | ------------------------------------------------- |
| `ReviewList` | The list of reviews loaded from the storage file. |

### Exceptions

| Exception     | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| `IOException` | Thrown when an error occurs during file creation or reading. |

### Example Usage

```java
ReviewList reviews = storage.loadReviews();
```

---

## `loadReviewsWithWarnings()`

### Description

Loads reviews from the storage file and returns both the loaded reviews and any non-fatal warnings encountered during loading.

This method uses **best-effort loading**:

* valid review blocks are loaded normally,
* malformed review blocks may be skipped,
* warnings are collected and returned instead of immediately failing the entire load.

This method is used when the caller needs both the loaded data and information about partial parsing issues.

### Parameters / Inputs

None.

### Return Value

| Type                | Description                                                        |
| ------------------- | ------------------------------------------------------------------ |
| `StorageLoadResult` | Contains the loaded `ReviewList` and a list of non-fatal warnings. |

### Exceptions

| Exception     | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| `IOException` | Thrown when an error occurs during file creation or reading. |

### Example Usage

```java
StorageLoadResult result = storage.loadReviewsWithWarnings();
ReviewList reviews = result.reviewList();
List<String> warnings = result.warnings();
```

---

## `saveReviews(ReviewList reviewList)`

### Description

Saves all reviews in the given `ReviewList` to the storage file.

The save operation rewrites the full contents of the storage file so that the file reflects the current in-memory review list.

### Parameters / Inputs

| Parameter    | Type         | Description                  |
| ------------ | ------------ | ---------------------------- |
| `reviewList` | `ReviewList` | The review list to be saved. |

### Return Value

None.

### Exceptions

| Exception     | Description                                      |
| ------------- | ------------------------------------------------ |
| `IOException` | Thrown when an error occurs during file writing. |

### Example Usage

```java
storage.saveReviews(reviews);
```

---

## `StorageLoadResult.reviewList()`

### Description

Returns the `ReviewList` loaded from storage.

### Parameters / Inputs

None.

### Return Value

| Type         | Description                                   |
| ------------ | --------------------------------------------- |
| `ReviewList` | The review list loaded from the storage file. |

### Example Usage

```java
ReviewList reviews = result.reviewList();
```

---

## `StorageLoadResult.warnings()`

### Description

Returns the list of non-fatal warnings encountered while loading from storage.

These warnings typically indicate that some parts of the storage file were malformed, but that loading was still able to proceed for valid review entries.

### Parameters / Inputs

None.

### Return Value

| Type           | Description                             |
| -------------- | --------------------------------------- |
| `List<String>` | The list of non-fatal loading warnings. |

### Example Usage

```java
List<String> warnings = result.warnings();
```

---

## Additional Notes

* The storage component stores reviews in a text file.
* The full `ReviewList` is written back to disk whenever `saveReviews(...)` is called.
* The storage component is designed to be tolerant of malformed saved data by using warning-based best-effort loading.
* Fatal file operation failures are surfaced through `IOException`, while non-fatal parsing issues are returned as warnings in `StorageLoadResult`.
