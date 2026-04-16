# Logic API Documentation

This file documents the important APIs exposed by `MealMeterController` to `MealMeterGui`.

---

## 1. `submitReview()`

### Description
Submits a user review based on the user input from `MealMeterGui` in the `Patron Feedback` tab.

### Parameters / Inputs
| Parameter          | Type     | Description                                                                                                          |
|--------------------|----------|----------------------------------------------------------------------------------------------------------------------|
| `reviewBody`       | `String` | User input from the `Your Review` text field                                                                         |
| `foodScore`        | `Double` | User input from the `Food Quality` spinner, clamped to a range of 1.0 to 5.0                                         |
| `cleanlinessScore` | `Double` | User input from the `Cleanliness` spinner, clamped to a range of 1.0 to 5.0                                          |
| `serviceScore`     | `Double` | User input from the `Service` spinner, clamped to a range of 1.0 to 5.0                                              |
| `tagsAsString`     | `String` | Comma-separated user input from the `Tags (Optional)` text field, specifying the `Tag`s to include with the `Review` |

### Return Value

| Type            | Description                                                                 |
|-----------------|-----------------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after submitting the review |

### Example Usage

`mealMeterController.submitReview("Good!", 5.0, 5.0, 5.0, "positive, fullScore");`

---

## 2. `filterReviews()`
### Description
Filters the reviews in the displayed `ReviewList` based on the user criteria specified in the `Owner Management` tab.

### Parameters / Inputs

| Parameter     | Type     | Description                                                                                                      |
|---------------|----------|------------------------------------------------------------------------------------------------------------------|
| `includeTags` | `String` | Comma-separated user input from the `Include Tags:` text field, specifying the `Tag`s to include with the filter |
| `excludeTags` | `String` | Comma-separated user input from the `Exclude Tags:` text field, specfiying the `Tag`s to exclude with the filter |
| `status`      | `String` | User input from the `Status` dropdown menu, specifying the status of `isResolved` to filter by                   |
| `conditions`  | `String` | Comma-separated user input from the `Conditions:` text field, specifying the `Condition`s to filter by           |

### Return Value

| Type            | Description                                                             |
|-----------------|-------------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after filtering reviews |

### Example Usage

`mealMeterController.filterReviews("positive", "negative", "Resolved", "cleanliness == 5");`

---

## 3. `sortReviews()`

### Description
Sorts the reviews in the displayed `ReviewList` based on specified user criteria in the `Owner Management` tab.

### Parameters / Inputs

| Parameter   | Type     | Description                                                                                       |
|-------------|----------|---------------------------------------------------------------------------------------------------|
| `sortBy`    | `String` | User input from the `Sort By:` dropdown menu, specifying the `Criterion` to sort by as a `String` |
| `sortOrder` | `String` | User input from the `Order:` dropdown menu, specifying the `SortOrder` to sort by as a `String`   |

### Return Value

| Type            | Description                                                           |
|-----------------|-----------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after sorting reviews |

### Example Usage

`mealMeterController.sortReviews("Food", "Descending");`

---

## 4. `resolveReview()`, `unresolveReview()`

### Description
Resolves or unresolves the selected review in the displayed `ReviewList` in the `Owner Management` tab. These methods
will resolve the indexing differences between the saved and displayed `ReviewList`.

The methods are executed on the selected `Review`  in the displayed `ReviewList` when the user presses the `Resolved` or
`Oustanding` buttons in the `Owner Management` tab.

### Parameters / Inputs

| Parameter          | Type         | Description                                             |
|--------------------|--------------|---------------------------------------------------------|
| `displayedReviews` | `ReviewList` | The displayed `ReviewList`                              |
| `rowIndex`         | `int`        | The index of the `Review` in the displayed `ReviewList` |

### Return Value

| Type            | Description                                                                                 |
|-----------------|---------------------------------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after resolving or unresolving the `Review` |

### Example Usage

`mealMeterController.resolveReview(reviews, 1);`

`mealMeterController.unresolveReview(reviews, 2);`

---

## 5. `addTags()`, `deleteTags()`

### Description
Adds or deletes new `Tag`s from the selected review in the displayed `ReviewList` in the `Owner Management` tab. These
methods will resolve the indexing differences between the saved and displayed `ReviewList`.

The methods are executed on the selected `Review` in the displayed `ReviewList` when the user presses the `Add Tags` or
`Delete Tags` buttons in the `Owner Management` tab.

### Parameters / Inputs

| Parameter          | Type         | Description                                             |
|--------------------|--------------|---------------------------------------------------------|
| `displayedReviews` | `ReviewList` | The displayed `ReviewList`                              |
| `rowIndex`         | `int`        | The index of the `Review` in the displayed `ReviewList` |
| `tags`             | `String`     | Comma-separated user input `Tag`s to add or delete      |

### Return Value

| Type            | Description                                                                                       |
|-----------------|---------------------------------------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after adding or deleting `Tag`s from the `Review` |

### Example Usage

`mealMeterController.addTags(reviews, 1, "tag1, tag2");`]

`mealMeterController.deleteTags(reviews, 2, "tag3");`

---

## 5. `deleteReview()`

### Description
Deletes the selected `Review` in the displayed `ReviewList` in the `Owner Management` tab. This method will resolve the
indexing differences between the saved and displayed `ReviewList`.

This method is executed on the selected `Review` in the displayed `ReviewList` when the user presses the `Delete` button 
in the `Owner Management` tab.

### Parameters / Inputs

| Parameter          | Type         | Description                                             |
|--------------------|--------------|---------------------------------------------------------|
| `displayedReviews` | `ReviewList` | The displayed `ReviewList`                              |
| `rowIndex`         | `int`        | The index of the `Review` in the displayed `ReviewList` |

### Return Value

| Type            | Description                                                                 |
|-----------------|-----------------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after deleting the `Review` |

### Example Usage

`mealMeterController.deleteReview(reviews, 1);`

---

## 6. `login()`

### Description
Logs the user in with the specified password. This grants the user access to the `Owner Management` tab.

### Parameters / Inputs
| Parameter  | Type     | Description                                                                                                                        |
|------------|----------|------------------------------------------------------------------------------------------------------------------------------------|
| `password` | `String` | The password entered by the user in the `Owner Login` window when the user is not logged in and enters the `Owner Management` tab. |

### Return Value

| Type            | Description                                                      |
|-----------------|------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after logging in |

### Example Usage
`mealMeterController.login("password");`

---

## 7. `logout()`

### Description
Logs the user out of the `Owner Management` tab when the `Logout` button is pressed. This revokes the user's access to
the `Owner Management` tab.

### Return Value

| Type            | Description                                                       |
|-----------------|-------------------------------------------------------------------|
| `CommandResult` | A `CommandResult` object containing information after logging out |

### Example Usage
`mealMeterController.logout();`