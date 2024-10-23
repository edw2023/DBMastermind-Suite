package com.pw.dcp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseVO {
    private String jdbcUrl;
    private String dbName;
    private String userName;
    private String password;

}
