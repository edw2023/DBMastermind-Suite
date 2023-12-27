package com.pw.dcp.service;

import org.springframework.stereotype.Service;


/*
* This class is used to validate the input DDL and DML statements
* It supports multiple database types: MySQL, Oracle, SQL Server, PostgreSQL, DB2, H2, Sybase, SQLite, MariaDB
* The validation includes syntax, safety and keyword checks
* The SQL parsing and syntax check is done by using the JSQLParser library
* The Safety checks are done by using JSQLParser and Self-defined rules
* The Self-defined rules are configurable in UI
* */
@Service
public class ValidationService {

}
