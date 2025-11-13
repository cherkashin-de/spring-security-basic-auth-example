package com.security.auth.model.request;

import java.io.Serializable;
import java.sql.Timestamp;

public record UserInformationUpdate(String username, String fullName,
                                    Timestamp birthDay,
                                    Double salary) implements Serializable {
}
