# SchemaDiff

**SchemaDiff** is a powerful tool designed to compare and analyze the differences between database schemas, making it easier to manage and migrate databases in various environments. It supports different types of databases including Oracle, MySQL, PostgreSQL, and more.

## Features

- **Cross-Platform Comparison**: Compare database schemas across different database types (e.g., Oracle to MySQL, PostgreSQL to MySQL, etc.).
- **Comprehensive Diffing**: Supports table structures, indexes, stored procedures, sequences, triggers, and more.
- **User-Friendly Interface**: Easy to use and integrates well with existing workflows.
- **Efficient Reports**: Generate detailed reports on schema differences to simplify migration or database synchronization.
- **Customizable**: Highly configurable to suit various project needs and database configurations.

## Supported Databases

- Oracle
- MySQL
- PostgreSQL
- SQL Server
- DB2
- Sybase

## Installation

### Requirements

- Java 8 or later
- Maven

### Steps to Install

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/SchemaDiff.git

2. Navigate to the project directory:
   ```bash
   cd SchemaDiff

3. Build the project using Maven:
   ```bash
   mvn clean install

### Usage

   To compare two database schemas, run the following command:
   ```bash
   java -jar SchemaDiff.jar <database1_connection_string> <database2_connection_string>

