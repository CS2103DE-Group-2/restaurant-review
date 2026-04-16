# Software Design Document (SDD)

## System Overview
MealMeter is a desktop-based restaurant feedback system intended for deployment at restaurant point-of-sale terminals or customer kiosks.

The system allows patrons to provide feedback through a simple interface consisting of:

- Rating inputs
- Text input for written reviews
- A submission action

Submitted feedback is processed by the application and stored locally for later viewing, sorting, filtering, and management by restaurant staff.

---

## Architecture Design
The system follows a **Model-View-Controller (MVC) design pattern** consisting of:

- **View: User Interface Layer**
- **Controller: Controller Layer**
- **Model: Model Layer**

This architecture separates user interaction, application logic, and data persistence so that each part of the system remains easier to maintain, extend, and test.

### Architecture Diagram
![Architecture Diagram](/docs/architecture/architecture-diagram.jpg)

---

## Major System Components

### 1. User Interface Layer

#### Responsibilities
- Displays the review submission form
- Displays the list of reviews
- Allows users to enter commands or interact through GUI controls
- Shows system messages and feedback to the user

#### Key Classes
- `MealMeterGui`
- `OwnerPanel`
- `PatronPanel`

---

### 2. Logic Layer

#### Responsibilities
- Determines how user input should be handled
- Parses user commands and executes the appropriate actions
- Validates user input
- Handles sorting and filtering of reviews
- Manages review objects and review collections
- Coordinates saving and loading through the storage layer

#### Key Classes
- `CommandParser`
- `Command`
- `Review`
- `ReviewList`
- `Rating`
- `Tag`
- `ArgumentParser`
- `FileParser`

---

### 3. Storage Layer

#### Responsibilities
- Loads reviews from a text file when the application starts
- Saves reviews to a text file when the review list is updated
- Handles file creation if the data file does not already exist

#### Key Classes
- `Storage`

---

## UML Diagrams

### Class Diagram
The class diagram below shows the main classes in the system and the relationships between them.

![Class Diagram](/docs/architecture/class-diagram.png)

### Sequence Diagrams
![Sequence Diagram](/docs/architecture/seq-diagram-init-and-end.png)
Initialisation and Program End

![Sequence Diagram](/docs/architecture/seq-diagram-add-review.png)
Adding a Review

![Sequence Diagram](/docs/architecture/seq-diagram-delete-review.png)
Deleting a Review

![Sequence Diagram](/docs/architecture/seq-diagram-add-tag.png)
Adding a Tag to a Review

![Sequence Diagram](/docs/architecture/seq-diagram-sort.png)
Sorting Reviews

![Sequence Diagram](/docs/architecture/seq-diagram-filter.png)
Filtering Reviews

### Use Case Diagram

![Use Case Diagram](/docs/architecture/use-case-diagram.png)
Use Case Diagram

### Use Cases

<details>
<summary>UC01 – Add a Review</summary>

> **Software System:** MealMeter\
> **Use Case:** UC01 – Add a Review\
> **Actor:** Restaurant Patron\
> **Precondition:** Restaurant Patron is at the point of sale and is presented with the GUI\
> **MSS:**\
> **1.** Restaurant Patron enters a rating for the food, cleanliness, and service\
> **2.** MealMeter validates the rating inputs\
> **3.** Restaurant Patron enters a review\
> **4.** Restaurant Patron submits the review\
> **5.** MealMeter shows a success message\
> **6.** MealMeter saves the review to the data file\
> Use case ends.
> 
> **Extensions:**\
> **2a.** MealMeter detects an invalid rating input\
> **2a1.** MealMeter shows an error message\
> **2a2.** Restaurant Patron is prompted to enter a valid rating\
> Steps 2a1 and 2a2 are repeated until the rating is valid.
> Use case resumes from step 3.
> 
> **\*a.** At any time, Restaurant Patron chooses to cancel the review submission\
> **\*a1.** MealMeter requests to confirm the cancellation\
> **\*a2.** Restaurant Patron confirms the cancellation\
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
> **3.** MealMeter validates the selection\
> **4.** MealMeter requests to confirm the deletion\
> **5.** Restaurant Owner confirms the deletion\
> **6.** MealMeter deletes the review from the data file\
> **7.** MealMeter shows a success message\
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

</details>

---

## Design Details

### `Ui`
The `Ui` class handles interaction with the user. It is responsible for collecting user input and displaying output messages.

**Main responsibilities:**
- Read user input
- Display system messages

### `CommandParser`
The `CommandParser` class interprets raw user input and converts it into a `Command` object that can be executed by the system.

**Main responsibilities:**
- Parse command keywords
- Extract command arguments
- Construct the appropriate `Command`

### `Command`
The `Command` class represents an executable user instruction. Each command stores the information needed for execution.

**Main responsibilities:**
- Represent a user action
- Execute logic on the review list
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
Feedback is collected directly at the point of sale. This improves response rates and helps restaurants capture impressions while the dining experience is still fresh in the patron’s mind.

### Layered Architecture
A 3-tier architecture was chosen to separate presentation, logic, and storage concerns. This improves modularity and makes future enhancements easier to implement.

### Local File Storage
The system uses local file-based storage instead of a server-based backend. This keeps deployment simple and aligns with the intended lightweight desktop application design.

---

## Planned Diagrams to Include
The following supporting diagrams should be added to complete this document:

- Sequence diagram for deleting a review
- Use case diagram