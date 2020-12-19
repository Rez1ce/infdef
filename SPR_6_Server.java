package lab4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;


public class SPR_6_Server
{

    private final BigInteger k;
    private final BigInteger safePrime;
    private final BigInteger generator;

    private final ArrayList<Person> serverPersonList;


    public SPR_6_Server(BigInteger N, BigInteger g, BigInteger k)
    {
        this.safePrime = N;
        this.generator = g;
        this.k = k;

        serverPersonList = new ArrayList<>();
    }


    public void processRegistration(String username, String salt, BigInteger verifier) throws Exception
    {
        Person currentPerson = null;
        for (Person person : serverPersonList)
        {
            currentPerson = person;
            if (currentPerson.username.compareTo(username) == 0)
                throw new Exception("Such client already exists");
        }

        if(currentPerson == null)
        {
            String saltNew = salt.replace("\n", "\\n");
            System.out.println("Server received : username = " + username + "\tsalt =" + saltNew + "\tverifier = " + verifier);

            serverPersonList.add(new Person(username, salt, verifier));

            System.out.println("Server remembered new User");
        }
    }


    public void processLogin(SPR_6_Client client, String username, BigInteger authentication) throws Exception
    {
        if(!authentication.equals(BigInteger.ZERO))
        {
            Person currentPerson = null;
            for (Person person : serverPersonList)
            {
                currentPerson = person;
                if (currentPerson.username.compareTo(username) == 0)
                {
                    System.out.println("Server received : username = " + username + "\tauthentication = " + authentication);

                    currentPerson.setAuthentication(authentication);
                    currentPerson.setB(generateB());
                    currentPerson.setBackAuthentication(generateBackAuthentication(currentPerson.verifier, currentPerson.b));

                    String salt = currentPerson.salt.replace("\n", "\\n");
                    System.out.println("\nServer generated :\n\tb = " + currentPerson.b + "\n\tbackAuthentication = " + currentPerson.backAuthentication);
                    System.out.println("\nServer sent to client : salt = " + salt + "\tbackAuthentication = " + currentPerson.backAuthentication );
                    client.receiveLoginRequest(currentPerson.salt, currentPerson.backAuthentication);

                    currentPerson.setU(generateU(currentPerson.authentication, currentPerson.backAuthentication));
                    currentPerson.setK(generateKey(currentPerson.authentication, currentPerson.verifier, currentPerson.b, currentPerson.u));

                    System.out.println("\nServer generated : \n\tu = " + currentPerson.u + "\n\tkey = " + currentPerson.K);

                    BigInteger clientM = receiveFinalKey(client.sendFinalKey());

                    System.out.println("Server received : finalKey = " + clientM);

                    currentPerson.setM(generateM(currentPerson.username, currentPerson.salt, currentPerson.authentication,
                            currentPerson.backAuthentication, currentPerson.K));

                    System.out.println("\nServer generated : \n\tfinalKey = " + currentPerson.M);

                    if(clientM.equals(currentPerson.M))
                    {
                        System.out.println("\nServer compared finalKey, the are identical");

                        BigInteger request = generateRequest(currentPerson.authentication, currentPerson.M, currentPerson.K);

                        System.out.println("\nServer generated : \n\trequest = " + request);
                        System.out.println("\nServer sent to client : request = " + request);

                        client.receiveConfirmationRequest(request);
                    }
                    else
                        throw new Exception("Error, final keys don't match");


                    return;
                }

            }

            if(currentPerson == null)
                throw new Exception("No such person");
        }
        else
            throw new Exception("Error, authentication is zero");

    }

    private BigInteger receiveFinalKey(BigInteger M) { return M; }


    private BigInteger generateM(String username, String salt, BigInteger authentication, BigInteger back_authentication, BigInteger K) { return SPR_6.hash(SPR_6.hash(this.safePrime).xor(SPR_6.hash(this.generator)), SPR_6.hash(username), salt, authentication, back_authentication, K); }
    private BigInteger generateKey(BigInteger authentication, BigInteger verifier, BigInteger b, BigInteger u) { return SPR_6.hash(authentication.multiply(verifier.modPow(u, safePrime)).modPow(b, safePrime)); }
    public BigInteger generateRequest(BigInteger authentication, BigInteger M, BigInteger K) { return SPR_6.hash(authentication, M, K); }
    private BigInteger generateB() { return new BigInteger(512, new Random()); }
    public BigInteger generateBackAuthentication(BigInteger currentPersonVerifier, BigInteger b) { return (this.k.multiply(currentPersonVerifier).add(this.generator.modPow(b, this.safePrime))).mod(this.safePrime); }


    public BigInteger generateU(BigInteger authentication, BigInteger currentPersonBackAuthentication) throws Exception
    {
        BigInteger u = SPR_6.hash(authentication, currentPersonBackAuthentication);

        if (u.equals(BigInteger.ZERO))
            throw new Exception("Error, scrambler equals ZERO");
        else
            return u;
    }


    static class Person
    {
        private final String username;
        private final String salt;

        private final BigInteger verifier;
        private BigInteger authentication;
        private BigInteger backAuthentication;

        private BigInteger b;
        private BigInteger u;
        private BigInteger K;
        private BigInteger M;

        public void setK(BigInteger k) { this.K = k; }
        public void setM(BigInteger m) { this.M = m; }
        public void setU(BigInteger u) { this.u = u; }
        public void setB(BigInteger b) { this.b = b; }

        public void setAuthentication(BigInteger authentication) { this.authentication = authentication; }
        public void setBackAuthentication(BigInteger backAuthentication) { this.backAuthentication = backAuthentication; }


        public Person(String username, String salt, BigInteger verifier)
        {
            this.username = username;
            this.salt = salt;
            this.verifier = verifier;
        }
    }
}