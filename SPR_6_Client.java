package lab4;

import java.math.BigInteger;
import java.util.Random;

public class SPR_6_Client
{
    private final BigInteger safePrime;
    private final BigInteger generator;
    private final BigInteger k;

    private BigInteger verifier;

    private final String username;
    private final String password;
    private String salt;

    private BigInteger authentication;
    private BigInteger a;


    private BigInteger x;
    private BigInteger u;


    private BigInteger backAuthentication;
    private BigInteger M;
    private BigInteger K;


    private final SPR_6_Server server;


    public SPR_6_Client(BigInteger safePrime, BigInteger generator, BigInteger k, String username, String password, SPR_6_Server server)
    {
        this.safePrime = safePrime;
        this.generator = generator;
        this.k = k;

        this.username = username;
        this.password = password;

        this.server = server;

        fillBaseData();
    }


    public String generateSalt()
    {
        StringBuilder s = new StringBuilder();

        for(int i = 0 ; i < 10 + Math.random() * 30 ; i++)
            s.append((char) (0 + Math.random() * 255));

        return s.toString();
    }

    public void sendRegistration() throws Exception
    {
        String salt = this.salt.replace("\n", "\\n");
        System.out.println("\nClient sent to server : username = " + this.username + "\tsalt = " + salt + "\tverifier = " + this.verifier);
        server.processRegistration(this.username, this.salt, this.verifier);
    }

    private void fillBaseData()
    {

        salt = generateSalt();
        x = SPR_6.hash(salt, password);
        verifier = generator.modPow(x, safePrime);

        String salt = this.salt.replace("\n", "\\n");
        System.out.println("\nClient generated :\n\tsalt = " + salt + "\n\tverifier = " + this.verifier);
    }


    public void sendLogin() throws Exception
    {
        generateA();
        generateAuthentication();
        System.out.println("\nClient generated :\n\ta = " + this.a + "\n\tauthentication = " + this.authentication);
        System.out.println("\nClient sent to server : username = " + this.username + "\tauthentication = " + this.authentication);
        server.processLogin(this, this.username, this.authentication);
    }


    public void receiveLoginRequest(String salt, BigInteger backAuthentication) throws Exception
    {
        if(salt.compareTo(this.salt) == 0)
        {
            if(!backAuthentication.equals(BigInteger.ZERO))
            {
                this.backAuthentication = backAuthentication;
                generateU(backAuthentication);
                generateKey(backAuthentication);

                salt = salt.replace("\n", "\\n");
                System.out.println("Client received : salt = " + salt + "\tbackAuthentication = " + backAuthentication);
                System.out.println("\nClient generated : \n\tu = " + this.u + "\n\tkey = " + this.K);

            }
            else
                throw new Exception("Error, back_authentication is ZERO");
        }
        else
            throw new Exception("Error salt doesn't match");


    }

    public BigInteger sendFinalKey()
    {
        this.M = generateFinalKey();
        System.out.println("\nClient generated : \n\tfinalKey = " + this.M);
        System.out.println("\nClient sent to server : finalKey = " + this.M);

        return this.M;
    }




    public void receiveConfirmationRequest(BigInteger request) throws Exception
    {
        if(request.equals(SPR_6.hash(authentication, M, K)))
        {
            System.out.println("Client received : request = " + request);
            System.out.println("\nAuthentication finished successfully!!!");
        }
        else
            throw new Exception("Error, R doesn't match");
    }




    public void generateU(BigInteger back_authentication) throws  Exception
    {
        u = SPR_6.hash(authentication, back_authentication);
        if (u.equals(BigInteger.ZERO))
            throw new Exception();
    }


    public void generateAuthentication() { authentication = generator.modPow(a, safePrime); }
    private void generateA() { a = new BigInteger(512, new Random()); }

    public void generateKey(BigInteger back_authentication) { K = SPR_6.hash((back_authentication.subtract(k.multiply(generator.modPow(x, safePrime)))).modPow(a.add(u.multiply(x)), safePrime)); }

    private BigInteger generateFinalKey() { return SPR_6.hash(SPR_6.hash(safePrime).xor(SPR_6.hash(generator)), SPR_6.hash(username), salt, authentication, backAuthentication , K); }
}