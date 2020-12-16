import math

p = 3
q = 7
n = p*q
f_E = (p - 1) * (q - 1)
e = 5
# {e, n} - open key

# find 'd' for secret key
temp = 0
d = 30 #random
while(temp != 1):
    temp = (d * e) % f_E
    if(temp != 1):
        d += 1
#some kind of my random but very lazy :D

# {d, n} - secret key

some_message = 5

crypt_data = math.pow(some_message, e) % n
print("{",e, n, "} - open key")
print("{",d, n, "} - secret key")
print(crypt_data, " - crypted message")

decrypt_data = math.pow(crypt_data, e) % n

print(decrypt_data, "decrypted message")
