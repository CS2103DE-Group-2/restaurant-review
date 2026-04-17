# Software Design Document (SDD)

---

## System Overview
MealMeter is a desktop-based restaurant feedback system intended for deployment at restaurant point-of-sale terminals or customer kiosks.

The system allows patrons to provide feedback through a simple interface consisting of:

- Rating inputs
- Text input for written reviews
- A submission action

Submitted feedback is processed by the application and stored locally for later viewing, sorting, filtering, and management by restaurant staff.

---

## Architecture Design
A `Main` class is the main entry point of the application, which initialises the UI and controller.

The system follows a 4-layer architecture structure consisting of:

- `Ui`: the UI of the application
- `Logic`: The command execution layer
- `Model`: Holds the data of the application in memory
- `Storage`: Reads data from, and writes data to, the hard disk

### Architecture Diagram

![Architecture Diagram](architecture/architecture-diagram.jpg)

---

## Major System Components

### 1. `Ui` Component

#### Responsibilities
- Displays the Patron Feedback Panel to submit reviews
- Displays the Owner Management Panel to manage reviews
- Allows users to interact with the model via GUI elements, with the Controller acting as the intermediary
- Shows system messages and feedback after an action is completed

#### Key Classes
- `MealMeterGui`
- `OwnerPanel`
- `PatronPanel`

---

### 2. `Logic` Component

#### Responsibilities
- Parses user input and converts it into command objects
- Executes user actions as commands on the model
- Validates user input
- Handles sorting and filtering of reviews
- Manages review objects and review collections
- Coordinates saving and loading through the storage layer
- Handles user authentication

#### Key Classes
- `AuthManager`
- `Command`
- `Condition`
- `MealMeterController`
- `ArgumentParser`
- `ConditionParser`
- `Criterion`
- `SortOrder`

---

### 3. `Model` Component

#### Responsibilities
- Stores and manages review objects
- Manages review collections
- Manages reviews

#### Key Classes
- `Rating`
- `Review`
- `ReviewList`
- `Tag`

### 4. Storage Layer

#### Responsibilities
- Loads reviews from a text file when the application starts
- Saves reviews to a text file when the review list is updated
- Handles file creation if the data file does not already exist

#### Key Classes
- `Storage`

---

## UML Diagrams

#### `Ui` Class Diagram
![Ui Class Diagram](architecture/ui-class-diagram.png)

The above depicts the class diagram of the `Ui` component. This component is responsible for displaying the GUI of the
application. It is made up of the `MealMeterGui` class, which contains the two screens (`PatronPanel` and `OwnerPanel`)
available to the user.

The `MealMeterGui` class communicates with the `Model` component via the `Logic` component.

#### `Logic` Class Diagram
![Logic Class Diagram](architecture/logic-class-diagram.png)

The above depicts a (partial) class diagram of the `Logic` component. Not depicted are the `Command` subclasses and
their individual dependencies to the enums and the model components.

As seen in the diagram, the main activity of this component is to intepret user input from the `Ui` and execute the
corresponding command on the `Model`. This is done so via `MealMeterController`, which creates the appropriate `Command`
(e.g. `AddReviewCommand`) object and executes it, returing a `CommandResult` object to the `Ui` to display to the user.

The `AuthManager` class is responsible for managing the authentication state of the application. Based on administrator
requirements, `Command` objects may require authentication to be executed.

The `Condition` subclasses are used by `FilterReviewsCommand`, while the `Criterion` and `SortOrder` enumerations are
used by `SortReviewsCommand`.

Any `Command` that changes the `Model` will invoke `Storage` to save the updated model to the data file.

### `Model` Class Diagram
![Model Class Diagram](architecture/model-class-diagram.png)

The above depicts the class diagram of the `Model` component. This component stores the `Review` information of the
application in memory. The `Model` offers wrapper methods for the `Logic` component to access and manipulate the data.

The `Model` loaded into memory is done by the `Storage` component on application initialisation and is saved by the
`Command` objects whenever a change is made to the data via the `Storage` component.

### `Storage` Class Diagram
![Storage Class Diagram](architecture/storage-class-diagram.png)

The above depicts the class diagram of the `Storage` component. This component is responsible for loading and saving
`Review` objects to the data file. During initialisation, the `Storage` component will attempt to load reviews from
the specified path (default: `data/reviews.txt`).

A `StorageLoadResult` object is returned to the `Logic` component to
indicate if any errors occurred during loading or if the data file was successfully loaded, as well as the loaded
`ReviewList`.

### Sequence Diagrams
This section depicts the sequence diagrams of the application. The diagrams are non-exhaustive and only show the
important parts of the application. For brevity, the sequence diagrams do not depict internal `MealMeterGui`
interactions.

#### Feature: Adding a Review
![Sequence Diagram](architecture/seq-diagram-add-review.png)

---

#### Feature: Deleting a Review
![Sequence Diagram](architecture/seq-diagram-delete-review.png)

---

#### Feature: Adding Tags to a Review
![Sequence Diagram](architecture/seq-diagram-add-tag.png)

The constructor of `Tag` is not shown in the diagram but is called when the static method `toTags()` is called.

---

#### Feature: Sorting Reviews
![Sequence Diagram](architecture/seq-diagram-sort.png)

---

#### Feature: Filtering Reviews
![Sequence Diagram](architecture/seq-diagram-filter.png)

The constructors of `Tag`, `Condition` and `Criterion` are not shown in the diagram.
The `Tag` constructor is called when the static method `toTags()` is called.
The `Condition` and `Criterion` constructors are called when the static method `toConditions()` is called.
---

### Use Case Diagram

![Use Case Diagram](architecture/use-case-diagram.png)

The above depicts the use case diagram of the application. The application is designed to be used by both restaurant
patrons and restaurant owners. However, most of the features are only available to restaurant owners, with patrons only
being able to leave a review. To access the owner features, the user must log in.

Additionally, as stated in the [Logic class diagram section](#logic-class-diagram), only actions that modify the model
will invoke the storage layer to save the updated model to the data file.

### Use Cases

The application supports the following use cases. This section is non-exhaustive and only shows the most
important parts of the application.

<details>
<summary>UC01 – Add a Review</summary>

> **Software System:** MealMeter\
> **Use Case:** UC01 – Add a Review\
> **Actor:** Restaurant Patron\
> **Precondition:** Restaurant Patron is at the point of sale and is presented with the GUI\
> **MSS:**\
> **1.** Restaurant Patron enters a rating for the food, cleanliness, and service\
> **2.** Restaurant Patron enters a review\
> **3.** Restaurant Patron submits the review\
> **4.** MealMeter validates the review inputs\
> **5.** MealMeter shows a success message\
> **6.** MealMeter saves the review to the data file\
> Use case ends.
> 
> **Extensions:**\
> **4a.** MealMeter detects an invalid review input\
> **4a1.** MealMeter shows an error message\
> **4a2.** Restaurant Patron is prompted to enter a valid review\
> Steps 4a1 and 4a2 are repeated until the review is valid.
> Use case resumes from step 5.
> 
> **\*a.** At any time, Restaurant Patron chooses to clear the review submission form\
> **\*a1.** MealMeter clears the review submission form\
> Use case ends.
</details>

<details>
<summary>UC02 – Delete a Review</summary>

> **Software System:** MealMeter\
> **Use Case:** UC02 – Delete a Review\
> **Actor:** Restaurant Owner\
> **Precondition:** Restaurant Owner is logged in\
> **Guarantee:** The review is deleted from the list of reviews\
> **MSS:**\
> **1.** Restaurant Owner views all reviews\
> **2.** Restaurant Owner selects the review to be deleted\
> **3.** Restaurant Owner presses the delete button\
> **4.** MealMeter validates the selection\
> **5.** MealMeter deletes the review from the data file\
> **6.** MealMeter shows a success message\
> Use case ends.
> 
> **Extensions:**\
> **4a.** MealMeter detects an invalid selection\
> **4a1.** MealMeter shows an error message\
> **4a2.** Restaurant Owner is prompted to select a valid review\
> Steps 4a1 and 4a2 are repeated until the selection is valid.
> Use case resumes from step 5.
</details>

<details>
<summary>UC03 – Tag a Review</summary>

> **Software System:** MealMeter\
> **Use Case:** UC03 – Tag a Review\
> **Actor:** Restaurant Owner\
> **Precondition:** Restaurant Owner is logged in\
> **Guarantee:** The review is tagged with the new tag\
> **MSS:**\
> **1.** Restaurant Owner views all reviews\
> **2.** Restaurant Owner selects the review to be tagged\
> **3.** MealMeter validates the selection\
> **4.** MealMeter prompts the Restaurant Owner to enter the tag name\
> **5.** Restaurant Owner enters the tag name\
> **6.** Restaurant Owner submits the tag\
> **7.** MealMeter shows a success message\
> **8.** MealMeter saves the newly tagged review to the data file\
> Use case ends.
>
> **Extensions:**\
> **3a.** MealMeter detects an invalid selection\
> **3a1.** MealMeter shows an error message\
> **3a2.** Restaurant Owner is prompted to select a valid review\
> Steps 3a1 and 3a2 are repeated until the selection is valid.
> Use case resumes from step 4.
</details>

<details>
<summary>UC04 – Sort Reviews</summary>

> **Software System:** MealMeter\
> **Use Case:** UC04 – Sort Reviews\
> **Actor:** Restaurant Owner\
> **Precondition:** Restaurant Owner is logged in\
> **MSS:**\
> **1.** Restaurant Owner views all reviews\
> **2.** Restaurant Owner selects the sorting criterion\
> **3.** MealMeter sorts the reviews based on the criterion\
> **7.** MealMeter shows the new sorted list\
> Use case ends.

</details>

<details>
<summary>UC05 – Filter Reviews</summary>

> **Software System:** MealMeter\
> **Use Case:** UC05 – Filter Reviews\
> **Actor:** Restaurant Owner\
> **Precondition:** Restaurant Owner is logged in\
> **MSS:**\
> **1.** Restaurant Owner views all reviews\
> **2.** Restaurant Owner selects the filtering criterion\
> **3.** MealMeter filters the reviews based on the criterion\
> **7.** MealMeter shows the new filtered list\
> Use case ends.
>
> **Extensions:**\
> **2a.** MealMeter detects an invalid filter criterion\
> **2a1.** MealMeter shows an error message\
> **2a2.** Restaurant Owner is prompted to input a valid filter criterion\
> Steps 2a1 and 2a2 are repeated until the selection is valid.
> Use case resumes from step 3.

</details>

---

## Design Details

### `MealMeterGui`
The `MealMeterGui` class handles interaction with the user. It is responsible for collecting user input and displaying
output messages.

**Main responsibilities:**
- Read user input
- Display system messages

### `MealMeterController`
The `MealMeterController` class is responsible for executing user actions. The controller passes user input from
`MealMeterGui` to the respective `Command` objects and returns a `CommandResult` to `MealMeterGui` after the action is
completed.

**Main responsibilities:**
- Construct the appropriate `Command`
- Execute the command
- Return a `CommandResult` to `MealMeterGui`

### `Command`
The `Command` class represents an executable user instruction. Each command stores the information needed for execution.

**Main responsibilities:**
- Represent a user action
- Execute logic on the `ReviewList`
- Interact with storage when needed

### `Review`
The `Review` class represents a single customer review.

**Main responsibilities:**
- Store review body text
- Track resolution status
- Manage associated tags
- Maintain its associated rating

### `ReviewList`
The `ReviewList` class stores and manages multiple `Review` objects.

**Main responsibilities:**
- Add reviews
- Delete reviews
- Resolve or unresolve reviews
- Filter reviews
- Sort reviews
- Validate indices

### `Rating`
The `Rating` class stores structured rating information for a review.

**Rating categories:**
- Food
- Cleanliness
- Service

It also supports computing an overall rating value.

### `Tag`
The `Tag` class represents a label attached to a review for categorisation and filtering.

### `Storage`
The `Storage` class is responsible for persistent file handling.

**Main responsibilities:**
- Save reviews to file
- Load reviews from file
- Create the storage file if necessary

---

## Key Design Decisions

### Structured Rating Categories
The system uses structured rating categories:

- Food
- Cleanliness
- Service

This allows restaurants to analyse feedback more effectively than relying only on free-text reviews.

### Immediate Feedback Collection
Feedback is collected directly at the point of sale. This improves response rates and helps restaurants capture
impressions while the dining experience is still fresh in the patron’s mind.

### Layered Architecture
A 4-tier architecture was chosen to separate presentation, logic, and storage concerns. This improves modularity and
makes future enhancements easier to implement.

### Local File Storage
The system uses local file-based storage instead of a server-based backend. This keeps deployment simple and aligns
with the intended lightweight desktop application design.