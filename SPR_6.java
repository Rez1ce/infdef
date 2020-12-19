package lab4;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SPR_6
{
    SPR_6()
    {

        BigInteger q = BigInteger.probablePrime(16, new SecureRandom());
        BigInteger safePrime = BigInteger.TWO.multiply(q).add(BigInteger.ONE);

        BigInteger generator = BigInteger.valueOf(2);
        BigInteger k = new BigInteger("3");

        System.out.println("Generated :\n\tsafePrime = " + safePrime + "\n\tgenerator = " + generator + "\n\tk = " + k);

        SPR_6_Server server = new SPR_6_Server(safePrime, generator, k);

        String username = "userMy56Name";
        String password = "pass:e_2020";

        System.out.println("\nCreated client : \n\tusername = " + username + "\n\tpassword = " + password);
        SPR_6_Client client = new SPR_6_Client(safePrime, generator, k, username, password, server);

        try
        {
            client.sendRegistration();
            client.sendLogin();

        } catch (Exception e) { System.err.println(e.getMessage()); }
    }


    private static String bytesToHex(byte[] hash)
    {
        StringBuilder hexString = new StringBuilder();

        for (byte hash_byte : hash)
        {
            String hex = Integer.toHexString(0xff & hash_byte);

            if (hex.length() == 1)
                hexString.append('0');

            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static BigInteger hash(Object... input)
    {
        try
        {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            for (Object i : input)
            {
                if (i instanceof String)
                    sha256.update(((String) i).getBytes());
                else if (i instanceof BigInteger)
                    sha256.update(((BigInteger) i).toString(10).getBytes());
                else if (i instanceof byte[])
                    sha256.update((byte[]) i);
                else
                    throw new IllegalArgumentException();
            }
            return new BigInteger(bytesToHex(sha256.digest()), 16);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }

}