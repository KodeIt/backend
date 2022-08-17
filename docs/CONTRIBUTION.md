# Contributing guidelines

## Getting started 🚀

-   If it is your first time working on this project, it is recommended to start working on issues labelled `good first issue`.
-   Choose an issue that is Available and claim it in the comment section of the respective issue.
-   Once the maintainer of the repository assigns it to you, you can start working on it.
-   Make sure you keep updating us on your work.
-   Any changes made must be on a new branch created on the local repository. The name must be synonynous to the issue title.
-   Please make sure to follow the [Commit Message Guidelines](/docs/COMMIT_MESSAGE_GUIDELINES.md) when creating commits.
-   If you are new to Open Source, please read the [Basic Commands](https://github.com/firstcontributions/first-contributions) to get started .

---

</br>

## Setup 🛠️

### Step 1

-   Fork this repository by clicking `fork button` on the top right corner of the page.
-   Clone the repository in your local machine by typing

    ```bash
    git clone https://github.com/<your-username>/kodeit-backend.git && cd kodeit-backend
    ```

    in your terminal(for mac/linux) or Git Bash (for windows).

-   Now create a new branch using
    ```bash
    git checkout -b <your-new-branch-name>
    ```

### Step 2

-   Download and install the latest versions of [Java](https://jdk.java.net/). \
    For Linux

    ```
    apt-get install openjdk-jre-17 openjdk-jdk-17
    ```

    For MacOS

    ```
    brew install openjdk-jre-17 openjdk-jdk-17
    ```

-   Configure the `application-dummy.properties` file under `src/main/resources/` and rename the file to `application.properties`

-   Run
    ```
    ./mvnw clean install
    ```

Now you can start working on the project.

### Step 3

-   To start the development server, run the following command in your terminal(for mac/linux) or Git Bash (for windows):
    ```bash
    ./mvnw spring-boot:run
    ```
-   You can now access the development server at [http://localhost:8080](http://localhost:8080) if the settings in application.properties were not changed.

## Pushing the code

Once done, push the code to your local repository

## Create a pull request

Mention the issue # in the title of the pull request.
