package org.acme.cipher;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class Sha256Hasher {
    public String createHashText(String text)
    {
        try
        {
            if (text.isBlank())
            {
                return "";
            }
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return convertBytesToHexadecimal(bytes);
        }
        catch (NoSuchAlgorithmException ex)
        {
            return "";
        }

    }

    private String convertBytesToHexadecimal(final byte[] messageDigest)
    {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);

        while (hexText.length() < 32)
        {
            hexText = "0".concat(hexText);
        }

        return hexText;
    }
}
