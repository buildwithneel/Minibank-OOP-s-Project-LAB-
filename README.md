# MiniBank
Java 17 console banking application built with Maven.

## Requirements
Install JDK 17 or newer and Maven. In VS Code install Extension Pack for Java.

## Run
Open this folder in VS Code. In a terminal run:
`mvn clean test`
`mvn package`
`java -jar target/oop-minibank-1.0.0.jar`

The application stores serialized accounts in `data/accounts.dat`, appends transactions to `logs/transactions.log`, and writes reports to `reports/end-of-day.txt`. These folders are created automatically. The first run starts with an empty store.

## Menu
Open account, deposit, withdraw, transfer, statement, list accounts, batch processing, generate report, and exit.

## GitHub
Create a private repository named `oop-minibank-<rollno>`, add the faculty account under Settings → Collaborators, then run:
`git init`
`git add .`
`git commit -m "Build MiniBank project"`
`git branch -M main`
`git remote add origin <your-repository-url>`
`git push -u origin main`

Do not commit real customer data or account files. Add `data/`, `logs/`, and generated reports to `.gitignore` if using real data.
