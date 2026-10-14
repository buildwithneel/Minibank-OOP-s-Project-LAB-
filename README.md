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
