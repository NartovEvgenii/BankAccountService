package org.nartov.utils;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class HashUtils {

    public String hashPinCode(Integer pinCode){
        return Hashing.sha256()
                .hashString(pinCode.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public boolean checkPinCode(Integer pinCode, String hashPinCode){
        return Hashing.sha256()
                .hashString(pinCode.toString(), StandardCharsets.UTF_8)
                .toString()
                .equals(hashPinCode);
    }

}
